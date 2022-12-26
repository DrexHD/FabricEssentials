package org.server_utilities.essentials.command.util;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.drex.message.api.Message;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;

import java.util.Collections;
import java.util.Map;

public abstract class OptionalTargetCommand<T, S> extends Command {

    private final String targetArgumentId;

    public OptionalTargetCommand(Properties properties) {
        this(properties, "target");
    }

    public OptionalTargetCommand(Properties properties, String targetArgumentId) {
        super(properties);
        this.targetArgumentId = targetArgumentId;
    }

    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        registerOptionalArgument(literal);
    }

    protected void registerOptionalArgument(ArgumentBuilder<CommandSourceStack, ?> argument) {
        argument.executes(ctx -> this.execute(ctx, getSelf(ctx), true));
        RequiredArgumentBuilder<CommandSourceStack, T> target = Commands.argument(targetArgumentId, getArgumentType());
        target.requires(predicate("other"));
        target.executes(ctx -> this.execute(ctx, getArgument(ctx, targetArgumentId), false));
        argument.then(target);
    }

    protected void sendFeedbackWithOptionalTarget(CommandContext<CommandSourceStack> ctx, S target, boolean self, String messageId) {
        sendFeedbackWithOptionalTarget(ctx, target, self, Collections.emptyMap(), messageId);
    }

    protected void sendFeedbackWithOptionalTarget(CommandContext<CommandSourceStack> ctx, S target, boolean self, Map<String, Component> placeholders, String messageId) {
        if (self) {
            ctx.getSource().sendSystemMessage(Message.message(join(messageId, "self"), placeholders));
        } else {
            ctx.getSource().sendSystemMessage(Message.message(join(messageId, "other"), placeholders, getTargetPlaceholderContext(target, ctx.getSource().getServer())));
            sendTargetFeedback(target, ctx.getSource().getServer(), Message.message(join(messageId, "victim"), placeholders, PlaceholderContext.of(ctx.getSource())));
        }
    }

    protected void sendQueryFeedbackWithOptionalTarget(CommandContext<CommandSourceStack> ctx, S target, boolean self, String messageId) {
        sendQueryFeedbackWithOptionalTarget(ctx, target, self, Collections.emptyMap(), messageId);
    }

    protected void sendQueryFeedbackWithOptionalTarget(CommandContext<CommandSourceStack> ctx, S target, boolean self, Map<String, Component> placeholders, String messageId) {
        if (self) {
            ctx.getSource().sendSystemMessage(Message.message(join(messageId, "self"), placeholders));
        } else {
            ctx.getSource().sendSystemMessage(Message.message(join(messageId, "other"), placeholders, getTargetPlaceholderContext(target, ctx.getSource().getServer())));
        }
    }

    protected abstract ArgumentType<T> getArgumentType();

    protected abstract S getArgument(CommandContext<CommandSourceStack> ctx, String string) throws CommandSyntaxException;

    protected abstract S getSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException;

    protected abstract void sendTargetFeedback(S target, MinecraftServer server, Component component);

    protected abstract int execute(CommandContext<CommandSourceStack> ctx, S target, boolean self) throws CommandSyntaxException;

    protected abstract PlaceholderContext getTargetPlaceholderContext(S target, MinecraftServer server);

}
