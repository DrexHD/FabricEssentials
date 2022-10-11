package org.server_utilities.essentials.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.datafixers.util.Either;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.NotNull;
import org.server_utilities.essentials.EssentialsMod;
import org.server_utilities.essentials.config.ConfigManager;
import org.server_utilities.essentials.config.EssentialsConfig;
import org.server_utilities.essentials.config.util.WaitingPeriodConfig;
import org.server_utilities.essentials.util.AsyncChunkLoadUtil;
import org.server_utilities.essentials.util.AsyncTeleportPlayer;
import org.server_utilities.essentials.util.KeyUtil;
import org.server_utilities.essentials.util.TeleportCancelException;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

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

    public void sendFailure(CommandSourceStack src, String subKey, Object... args) {
        src.sendFailure(Component.translatable(translation(subKey), args));
    }

    protected static CompletableFuture<Optional<ChunkAccess>> asyncTeleport(CommandSourceStack src, ServerLevel targetLevel, ChunkPos targetPos, WaitingPeriodConfig config) throws CommandSyntaxException {
        AsyncTeleportPlayer asyncTeleportPlayer = (AsyncTeleportPlayer) src.getPlayerOrException();
        if (asyncTeleportPlayer.isAsyncLoadingChunks()) {
            src.sendFailure(Component.translatable(KeyUtil.translation("async.active")));
            return CompletableFuture.completedFuture(Optional.empty());
        }
        CompletableFuture<Optional<ChunkAccess>> result = new CompletableFuture<>();
        asyncTeleportPlayer.setAsyncLoadingChunks(true);
        CompletableFuture<Void> waitFuture = asyncTeleportPlayer.delayedTeleport(src, config);
        CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> chunkAccessFuture = AsyncChunkLoadUtil.scheduleChunkLoadWithRadius(targetLevel, targetPos, 2, config.period * 20 + 20);
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
                result.complete(Optional.empty());
            } else {
                chunkAccessFuture.whenCompleteAsync((either, chunkThrowable) -> {
                    if (chunkThrowable != null) {
                        src.sendFailure(Component.translatable(KeyUtil.translation("async.error")));
                        LOGGER.error("An unknown error occurred, while loading the chunks", chunkThrowable);
                        result.complete(Optional.empty());
                    } else {
                        // TODO: check if player is still online / hasnt died / changed dimension
                        result.complete(either.left());
                    }
                }, src.getServer());
            }
        }, src.getServer());
        return result;
    }

    public static final SimpleCommandExceptionType WORLD_UNKNOWN = new SimpleCommandExceptionType(Component.translatable(KeyUtil.translation("location.world.unknown")));
    public static final int SUCCESS = 1;
    public static final int FAILURE = 0;

}
