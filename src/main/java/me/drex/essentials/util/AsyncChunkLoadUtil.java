package me.drex.essentials.util;

/*
 * https://github.com/RelativityMC/VMP-fabric/blob/ver/1.19/src/main/java/com/ishland/vmp/common/chunkloading/async_chunks_on_player_login/AsyncChunkLoadUtil.java
 * */

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import me.drex.essentials.mixin.async.IChunkMap;
import me.drex.essentials.mixin.async.IServerChunkCache;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AsyncChunkLoadUtil {

    public static final TicketType ASYNC_CHUNK_LOAD = register("essentials_async_chunk_load", 0L, false, TicketType.TicketUse.LOADING);

    public static CompletableFuture<ChunkResult<ChunkAccess>> scheduleChunkLoadWithRadius(ServerLevel world, ChunkPos pos, int radius) {
        return scheduleChunkLoadWithLevel(world, pos, 33 - radius);
    }

    // Load static block to register ticket type in registry
    public static void init() {}

    public static CompletableFuture<ChunkResult<ChunkAccess>> scheduleChunkLoadWithLevel(ServerLevel world, ChunkPos pos, int level) {
        if (!world.getServer().isSameThread()) {
            return CompletableFuture
                .supplyAsync(() -> scheduleChunkLoadWithLevel(world, pos, level), world.getServer())
                .thenCompose(Function.identity());
        }
        final ServerChunkCache chunkCache = world.getChunkSource();
        chunkCache.addTicketWithRadius(ASYNC_CHUNK_LOAD, pos, 33 - level);
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

    private static TicketType register(String string, long l, boolean bl, TicketType.TicketUse ticketUse) {
        return Registry.register(BuiltInRegistries.TICKET_TYPE, string, new TicketType(l, bl, ticketUse));
    }

}
