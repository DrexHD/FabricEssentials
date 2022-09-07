package org.server_utilities.essentials.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.server_utilities.essentials.EssentialsMod;
import org.server_utilities.essentials.config.ConfigManager;
import org.server_utilities.essentials.config.EssentialsConfig;
import org.slf4j.Logger;

import java.util.function.Predicate;

public abstract class Command {

    protected final Properties properties;
    public static final String PERMISSION_PREFIX = "fabric_essentials";
    protected static final String PERMISSION_DELIMITER = ".";
    protected static final String SELF_PERMISSION_SUFFIX = "self";
    protected static final String OTHER_PERMISSION_SUFFIX = "other";
    protected static final Logger LOGGER = EssentialsMod.LOGGER;

    public Command(@NotNull Properties properties) {
        this.properties = properties;
    }

    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        String[] literals = this.properties.literals();
        if (literals.length == 0) return;
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(literals[0]);
        register(builder);
        LiteralCommandNode<CommandSourceStack> root = dispatcher.register(builder);
        for (int i = 1; i < literals.length; i++) {
            dispatcher.register(Commands.literal(literals[i]).redirect(root));
        }
    }

    protected abstract void register(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder);

    public static Predicate<CommandSourceStack> permission(String... permission) {
        return commandSourceStack -> Permissions.check(commandSourceStack, createPermission(permission));
    }

    public static String createPermission(String... permission) {
        return PERMISSION_PREFIX + PERMISSION_DELIMITER + String.join(PERMISSION_DELIMITER, permission);
    }

    public static EssentialsConfig config() {
        return ConfigManager.INSTANCE.config();
    }

    public static void sendError(CommandSourceStack source, String translation, Object... args) {
        source.sendFailure(Component.translatable(translation, args));
    }

    public static void sendError(CommandContext<CommandSourceStack> ctx, String translation, Object... args) {
        ctx.getSource().sendFailure(Component.translatable(translation, args));
    }

    public static void sendFeedback(CommandSourceStack source, String translation, Object... args) {
        source.sendSuccess(Component.translatable(translation, args), false);
    }

    public static void sendFeedback(CommandContext<CommandSourceStack> ctx, String translation, Object... args) {
        ctx.getSource().sendSuccess(Component.translatable(translation, args), false);
    }

    public static void sendFeedback(ServerPlayer serverPlayer, String translation, Object... args) {
        serverPlayer.sendSystemMessage(Component.translatable(translation, args));
    }

    public static String toName(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        try {
            Entity entity = source.getEntityOrException();
            return entity.getScoreboardName();
        } catch (CommandSyntaxException e) {
            return "Console";
        }
    }

    public static final SimpleCommandExceptionType WORLD_DOESNT_EXIST = new SimpleCommandExceptionType(Component.translatable("text.fabric-essentials.location.world_doesnt_exist"));

}
