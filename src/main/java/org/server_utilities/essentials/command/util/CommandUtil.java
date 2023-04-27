package org.server_utilities.essentials.command.util;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import me.drex.message.api.Message;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.server_utilities.essentials.config.util.WaitingPeriodConfig;
import org.server_utilities.essentials.mixin.async.IServerChunkCache;
import org.server_utilities.essentials.util.AsyncChunkLoadUtil;
import org.server_utilities.essentials.util.AsyncTeleportPlayer;
import org.server_utilities.essentials.util.ComponentPlaceholderUtil;
import org.server_utilities.essentials.util.TeleportCancelException;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles;
import static org.server_utilities.essentials.EssentialsMod.LOGGER;
import static org.server_utilities.essentials.util.AsyncChunkLoadUtil.ASYNC_CHUNK_LOAD;

public class CommandUtil {

    private static final SimpleCommandExceptionType ERROR_NOT_SINGLE_PLAYER = new SimpleCommandExceptionType(Component.translatable("argument.player.toomany"));
    private static final SimpleCommandExceptionType NO_PLAYERS_FOUND = new SimpleCommandExceptionType(Component.translatable("argument.entity.notfound.player"));

    public static GameProfile getGameProfile(CommandContext<CommandSourceStack> context, String argument) throws CommandSyntaxException {
        Collection<GameProfile> profiles = getGameProfiles(context, argument);
        if (profiles.isEmpty()) {
            throw NO_PLAYERS_FOUND.create();
        } else {
            if (profiles.size() != 1) {
                throw ERROR_NOT_SINGLE_PLAYER.create();
            } else {
                return profiles.iterator().next();
            }
        }
    }

    public static final SuggestionProvider<CommandSourceStack> PROFILES_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.suggest(ctx.getSource().getOnlinePlayerNames(), builder);


    public static void teleportEntity(Entity target, ServerLevel targetLevel, BlockPos targetLocation) {
        int x = targetLocation.getX();
        int y = targetLocation.getY();
        int z = targetLocation.getZ();
        float yRot = target.getYRot();
        float xRot = target.getXRot();
        if (target instanceof ServerPlayer serverPlayer) {
            ChunkPos chunkPos = new ChunkPos(targetLocation);
            targetLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, serverPlayer.getId());
            serverPlayer.stopRiding();
            if (serverPlayer.isSleeping()) {
                serverPlayer.stopSleepInBed(true, true);
            }
            if (targetLevel == serverPlayer.serverLevel()) {
                serverPlayer.connection.teleport(x + 0.5, y, z + 0.5, yRot, xRot);
            } else {
                serverPlayer.teleportTo(targetLevel, x + 0.5, y, z + 0.5, yRot, xRot);
            }
        } else {
            if (targetLevel == target.level()) {
                target.moveTo(x, y, z, yRot, xRot);
            } else {
                target.unRide();
                Entity entity2 = target;
                target = target.getType().create(targetLevel);
                if (target == null) {
                    return;
                }
                target.restoreFrom(entity2);
                target.moveTo(x, y, z, yRot, xRot);
                entity2.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                targetLevel.addDuringTeleport(target);
            }
        }
    }

    public static CompletableFuture<ChunkAccess> asyncTeleport(CommandSourceStack src, ServerLevel level, ChunkPos pos, WaitingPeriodConfig config) throws CommandSyntaxException {
        AsyncTeleportPlayer asyncTeleportPlayer = (AsyncTeleportPlayer) src.getPlayerOrException();
        if (asyncTeleportPlayer.isAsyncLoadingChunks()) {
            src.sendFailure(Message.message("fabric-essentials.async.active"));
            return CompletableFuture.completedFuture(null);
        }
        final int RADIUS = 2;
        CompletableFuture<ChunkAccess> result = new CompletableFuture<>();
        asyncTeleportPlayer.setAsyncLoadingChunks(true);
        final ServerChunkCache chunkCache = level.getChunkSource();
        final DistanceManager ticketManager = chunkCache.chunkMap.getDistanceManager();
        CompletableFuture<Void> waitFuture = asyncTeleportPlayer.delayedTeleport(src, config);
        CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> chunkAccessFuture = AsyncChunkLoadUtil.scheduleChunkLoadWithRadius(level, pos, RADIUS);
        chunkAccessFuture.whenCompleteAsync((either, throwable) -> {
            asyncTeleportPlayer.setAsyncLoadingChunks(false);
        }, src.getServer());
        waitFuture.whenCompleteAsync((unused, waitingThrowable) -> {
            if (waitingThrowable != null) {
                asyncTeleportPlayer.setAsyncLoadingChunks(false);
                if (waitingThrowable instanceof TeleportCancelException exception) {
                    src.sendFailure(exception.getRawMessage());
                } else {
                    src.sendFailure(Message.message("fabric-essentials.teleport.wait.error", ComponentPlaceholderUtil.exceptionPlaceholders(waitingThrowable)));
                    LOGGER.error("An unknown error occurred, during waiting period", waitingThrowable);
                }
                result.cancel(false);
                ticketManager.removeTicket(ASYNC_CHUNK_LOAD, pos, 33 - RADIUS, Unit.INSTANCE);
                ((IServerChunkCache) chunkCache).invokeRunDistanceManagerUpdates();
            } else {
                chunkAccessFuture.whenCompleteAsync((either, chunkThrowable) -> {
                    if (chunkThrowable != null) {
                        src.sendFailure(Message.message("fabric-essentials.async.error", ComponentPlaceholderUtil.exceptionPlaceholders(chunkThrowable)));
                        LOGGER.error("An unknown error occurred, while loading the chunks", chunkThrowable);
                        result.cancel(false);
                    } else {
                        if (either.left().isPresent()) {
                            result.complete(either.left().get());
                        } else {
                            src.sendFailure(Message.message("fabric-essentials.async.not_loaded"));
                            LOGGER.error("Chunk not there when requested: {}", either.right().get());
                        }
                    }
                    ticketManager.removeTicket(ASYNC_CHUNK_LOAD, pos, 33 - RADIUS, Unit.INSTANCE);
                    ((IServerChunkCache) chunkCache).invokeRunDistanceManagerUpdates();
                }, src.getServer());
            }
        }, src.getServer());
        return result;
    }
}
