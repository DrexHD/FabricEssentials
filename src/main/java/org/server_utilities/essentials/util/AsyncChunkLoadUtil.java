package org.server_utilities.essentials.util;

/*
 * https://github.com/RelativityMC/VMP-fabric/blob/ver/1.19/src/main/java/com/ishland/vmp/common/chunkloading/async_chunks_on_player_login/AsyncChunkLoadUtil.java
 * */

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import net.minecraft.server.level.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.server_utilities.essentials.mixin.async.IChunkMap;
import org.server_utilities.essentials.mixin.async.IServerChunkCache;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AsyncChunkLoadUtil {

    public static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> scheduleChunkLoadWithRadius(ServerLevel world, ChunkPos pos, int radius, int timeout) {
        return scheduleChunkLoadWithLevel(world, pos, 33 - radius, timeout);
    }

    public static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> scheduleChunkLoadWithLevel(ServerLevel world, ChunkPos pos, int level, int timeout) {
        if (!world.getServer().isSameThread()) {
            return CompletableFuture
                    .supplyAsync(() -> scheduleChunkLoadWithLevel(world, pos, level, timeout), world.getServer())
                    .thenCompose(Function.identity());
        }
        final ServerChunkCache chunkCache = world.getChunkSource();
        final DistanceManager ticketManager = chunkCache.chunkMap.getDistanceManager();
        TicketType<Object> ticketType = TicketType.create("essentials_async_chunk_load", (unit, unit2) -> 0, timeout);
        ticketManager.addTicket(ticketType, pos, level, Unit.INSTANCE);
        ((IServerChunkCache) chunkCache).invokeRunDistanceManagerUpdates();
        final ChunkHolder chunkHolder = ((IChunkMap) chunkCache.chunkMap).invokeGetUpdatingChunkIfPresent(pos.toLong());
        if (chunkHolder == null) {
            throw new IllegalStateException("Chunk not there when requested");
        }
        final ChunkHolder.FullChunkStatus levelType = ChunkHolder.getFullChunkStatus(level);

        final CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> future = switch (levelType) {
            case INACCESSIBLE -> chunkHolder.getOrScheduleFuture(ChunkHolder.getStatus(level), world.getChunkSource().chunkMap);
            case BORDER -> chunkHolder.getFullChunkFuture().thenApply(either -> either.mapLeft(Function.identity()));
            case TICKING -> chunkHolder.getTickingChunkFuture().thenApply(either -> either.mapLeft(Function.identity()));
            case ENTITY_TICKING -> chunkHolder.getEntityTickingChunkFuture().thenApply(either -> either.mapLeft(Function.identity()));
        };
        future.whenCompleteAsync((unused, throwable) -> {
            if (throwable != null) throwable.printStackTrace();
            /*ticketManager.removeTicket(ticketType, pos, level, Unit.INSTANCE);
            ((IServerChunkCache) chunkCache).invokeRunDistanceManagerUpdates();*/
        }, world.getServer());
        return future;
    }

}
