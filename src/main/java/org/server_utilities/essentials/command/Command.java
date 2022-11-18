package org.server_utilities.essentials.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.NotNull;
import org.server_utilities.essentials.EssentialsMod;
import org.server_utilities.essentials.config.ConfigManager;
import org.server_utilities.essentials.config.EssentialsConfig;
import org.server_utilities.essentials.config.util.WaitingPeriodConfig;
import org.server_utilities.essentials.mixin.async.IServerChunkCache;
import org.server_utilities.essentials.util.AsyncChunkLoadUtil;
import org.server_utilities.essentials.util.AsyncTeleportPlayer;
import org.server_utilities.essentials.util.KeyUtil;
import org.server_utilities.essentials.util.TeleportCancelException;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import static org.server_utilities.essentials.util.AsyncChunkLoadUtil.ASYNC_CHUNK_LOAD;

public abstract class Command {

    protected final Properties properties;
    protected static final Logger LOGGER = EssentialsMod.LOGGER;
    protected static final Object[] EMPTY = new Object[]{};

    public Command(@NotNull Properties properties) {
        this.properties = properties;
    }

    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        String[] aliasLiterals = this.properties.alias();
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(properties.literal()).requires(predicate());
        register(builder);
        LiteralCommandNode<CommandSourceStack> root = dispatcher.register(builder);
        for (String aliasLiteral : aliasLiterals) {
            dispatcher.register(
                    Commands.literal(aliasLiteral)
                            .requires(builder.getRequirement())
                            .executes(builder.getCommand())
                            .redirect(root)
            );
        }
    }

    protected abstract void register(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder);

    public Predicate<CommandSourceStack> predicate(String... nodes) {
        return src -> {
            try {
                return Permissions.check(src, permission(nodes), 2);
            } catch (Throwable ignored) {
                // Fallback for datapack compatibility
                return src.hasPermission(2);
            }
        };
    }

    public boolean permission(CommandSourceStack src, String... permission) {
        return predicate(permission).test(src);
    }

    public String permission(String... permission) {
        return KeyUtil.permission("command", properties.literal(), join(permission));
    }

    public String translation(String... keys) {
        return KeyUtil.translation("command", properties.literal(), join(keys));
    }

    public String join(String... parts) {
        return KeyUtil.join(parts);
    }

    public static EssentialsConfig config() {
        return ConfigManager.INSTANCE.config();
    }

    public void sendSuccess(CommandSourceStack src, String subKey, Object... args) {
        src.sendSuccess(Component.translatable(translation(subKey), args), false);
    }

    public void sendSuccess(CommandSourceStack src, Object... args) {
        sendSuccess(src, null, args);
    }

    public void sendFailure(CommandSourceStack src, String subKey, Object... args) {
        src.sendFailure(Component.translatable(translation(subKey), args));
    }

    public void sendSystemMessage(Entity entity, String subKey, Object... args) {
        entity.sendSystemMessage(Component.translatable(translation(subKey), args));
    }

    public void sendSystemMessage(Entity entity, Object... args) {
        sendSystemMessage(entity, null, args);
    }

    protected static CompletableFuture<ChunkAccess> asyncTeleport(CommandSourceStack src, ServerLevel level, ChunkPos pos, WaitingPeriodConfig config) throws CommandSyntaxException {
        AsyncTeleportPlayer asyncTeleportPlayer = (AsyncTeleportPlayer) src.getPlayerOrException();
        if (asyncTeleportPlayer.isAsyncLoadingChunks()) {
            src.sendFailure(Component.translatable(KeyUtil.translation("async.active")));
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
                    src.sendFailure(Component.translatable(KeyUtil.translation("teleport.wait.error")));
                    LOGGER.error("An unknown error occurred, during waiting period", waitingThrowable);
                }
                result.cancel(false);
                ticketManager.removeTicket(ASYNC_CHUNK_LOAD, pos, 33 - RADIUS, Unit.INSTANCE);
                ((IServerChunkCache) chunkCache).invokeRunDistanceManagerUpdates();
            } else {
                chunkAccessFuture.whenCompleteAsync((either, chunkThrowable) -> {
                    if (chunkThrowable != null) {
                        src.sendFailure(Component.translatable(KeyUtil.translation("async.error")));
                        LOGGER.error("An unknown error occurred, while loading the chunks", chunkThrowable);
                        result.cancel(false);
                    } else {
                        if (either.left().isPresent()) {
                            result.complete(either.left().get());
                        } else {
                            src.sendFailure(Component.translatable(KeyUtil.translation("async.error")));
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

    public static final SimpleCommandExceptionType WORLD_UNKNOWN = new SimpleCommandExceptionType(Component.translatable(KeyUtil.translation("location.world.unknown")));
    public static final int SUCCESS = 1;
    public static final int FAILURE = 0;

}
