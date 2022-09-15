package org.server_utilities.essentials.util;

/*
 * https://github.com/RelativityMC/VMP-fabric/blob/ver/1.19/src/main/java/com/ishland/vmp/common/chunkloading/async_chunks_on_player_login/AsyncChunkLoadUtil.java
 * */

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.mixin.async.IChunkMap;
import org.server_utilities.essentials.mixin.async.IServerChunkCache;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class AsyncChunkLoadUtil {

    private static final TicketType<Unit> ASYNC_CHUNK_LOAD = TicketType.create("essentials_async_chunk_load", (unit, unit2) -> 0);

    public static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> scheduleChunkLoad(ServerLevel world, ChunkPos pos) {
        return scheduleChunkLoadWithRadius(world, pos, 3);
    }

    public static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> scheduleChunkLoadWithRadius(ServerLevel world, ChunkPos pos, int radius) {
        return scheduleChunkLoadWithLevel(world, pos, 33 - radius);
    }

    public static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> scheduleChunkLoadToStatus(ServerLevel world, ChunkPos pos, ChunkStatus status) {
        return scheduleChunkLoadWithLevel(world, pos, 33 + ChunkStatus.getDistance(status));
    }

    public static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> scheduleChunkLoadWithLevel(ServerLevel world, ChunkPos pos, int level) {
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
        final ChunkHolder.FullChunkStatus levelType = ChunkHolder.getFullChunkStatus(level);

        final CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> future = switch (levelType) {
            case INACCESSIBLE -> chunkHolder.getOrScheduleFuture(ChunkHolder.getStatus(level), world.getChunkSource().chunkMap);
            case BORDER -> chunkHolder.getFullChunkFuture().thenApply(either -> either.mapLeft(Function.identity()));
            case TICKING -> chunkHolder.getTickingChunkFuture().thenApply(either -> either.mapLeft(Function.identity()));
            case ENTITY_TICKING -> chunkHolder.getEntityTickingChunkFuture().thenApply(either -> either.mapLeft(Function.identity()));
        };
        future.whenCompleteAsync((unused, throwable) -> {
            if (throwable != null) throwable.printStackTrace();
            ticketManager.removeRegionTicket(ASYNC_CHUNK_LOAD, pos, level, Unit.INSTANCE);
            ((IServerChunkCache) chunkCache).invokeRunDistanceManagerUpdates();
        }, world.getServer());
        return future;
    }

    public static CompletableFuture<ChunkAccess> scheduleChunkLoadForCommand(CommandSourceStack src, ServerLevel world, ChunkPos pos) throws CommandSyntaxException {
        return scheduleChunkLoadForCommand(src, world, pos, ignored -> {});
    }

    public static CompletableFuture<ChunkAccess> scheduleChunkLoadForCommand(CommandSourceStack src, ServerLevel world, ChunkPos pos, Consumer<Throwable> onFailure) throws CommandSyntaxException {
        CompletableFuture<ChunkAccess> future = new CompletableFuture<>();
        src.getPlayerOrException().displayClientMessage(Component.translatable("text.fabric-essentials.async.loading_chunks"), true);
        scheduleChunkLoadWithRadius(world, pos, 2).whenCompleteAsync((either, throwable) -> {
            if (throwable != null) {
                src.sendFailure(Component.translatable("text.fabric-essentials.async.error", throwable.getMessage()));
                onFailure.accept(throwable);
                return;
            }
            try {
                ChunkAccess chunkAccess = either.orThrow();
                future.complete(chunkAccess);
            } catch (Throwable throwable1) {
                src.sendFailure(Component.translatable("text.fabric-essentials.async.error", throwable1.getMessage()));
                onFailure.accept(throwable1);
            }
        }, src.getServer());
        return future;
    }

}
