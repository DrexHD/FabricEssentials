package org.server_utilities.essentials.util;

/*
 * https://github.com/RelativityMC/VMP-fabric/blob/ver/1.19/src/main/java/com/ishland/vmp/common/chunkloading/async_chunks_on_player_login/AsyncChunkLoadUtil.java
 * */

import com.mojang.datafixers.util.Unit;
import net.minecraft.server.level.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.server_utilities.essentials.mixin.async.IChunkMap;
import org.server_utilities.essentials.mixin.async.IServerChunkCache;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AsyncChunkLoadUtil {

    public static final TicketType<Unit> ASYNC_CHUNK_LOAD = TicketType.create("essentials_async_chunk_load", (unit, unit2) -> 0);


    public static CompletableFuture<ChunkResult<ChunkAccess>> scheduleChunkLoadWithRadius(ServerLevel world, ChunkPos pos, int radius) {
        return scheduleChunkLoadWithLevel(world, pos, 33 - radius);
    }

    public static CompletableFuture<ChunkResult<ChunkAccess>> scheduleChunkLoadWithLevel(ServerLevel world, ChunkPos pos, int level) {
        if (!world.getServer().isSameThread()) {
            return CompletableFuture
                .supplyAsync(() -> scheduleChunkLoadWithLevel(world, pos, level), world.getServer())
                .thenCompose(Function.identity());
        }
        final ServerChunkCache chunkCache = world.getChunkSource();
        final DistanceManager ticketManager = chunkCache.chunkMap.getDistanceManager();
        ticketManager.addTicket(ASYNC_CHUNK_LOAD, pos, level, Unit.INSTANCE);
        ((IServerChunkCache) chunkCache).invokeRunDistanceManagerUpdates();
        final ChunkHolder chunkHolder = ((IChunkMap) chunkCache.chunkMap).invokeGetUpdatingChunkIfPresent(pos.toLong());
        if (chunkHolder == null) {
            throw new IllegalStateException("Chunk not there when requested");
        }
        final FullChunkStatus levelType = ChunkLevel.fullStatus(level);
        final CompletableFuture<ChunkResult<ChunkAccess>> future = switch (levelType) {
            case INACCESSIBLE ->
                chunkHolder.scheduleChunkGenerationTask(ChunkLevel.generationStatus(level), world.getChunkSource().chunkMap);
            case FULL ->
                chunkHolder.getFullChunkFuture().thenApply(levelChunkChunkResult -> levelChunkChunkResult.map(levelChunk -> (ChunkAccess) levelChunk));
            case BLOCK_TICKING ->
                chunkHolder.getTickingChunkFuture().thenApply(levelChunkChunkResult -> levelChunkChunkResult.map(levelChunk -> (ChunkAccess) levelChunk));
            case ENTITY_TICKING ->
                chunkHolder.getEntityTickingChunkFuture().thenApply(levelChunkChunkResult -> levelChunkChunkResult.map(levelChunk -> (ChunkAccess) levelChunk));
        };
        future.whenCompleteAsync((unused, throwable) -> {
            if (throwable != null) throwable.printStackTrace();
        }, world.getServer());
        return future;
    }

}
