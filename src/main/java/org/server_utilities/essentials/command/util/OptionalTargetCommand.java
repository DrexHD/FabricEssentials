package org.server_utilities.essentials.command.util;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;

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
        String permission = this.properties.permission();
        if (permission != null) {
            target.requires(predicate("other"));
        }
        target.executes(ctx -> this.execute(ctx, getArgument(ctx, targetArgumentId), false));
        argument.then(target);
    }

    protected void sendFeedbackWithOptionalTarget(CommandContext<CommandSourceStack> ctx, S target, boolean self, Object[] selfArgs, Object[] otherArgs, Object[] victimArgs, String... keys) {
        if (self) {
            sendSuccess(ctx.getSource(), join(join(keys), "self"), selfArgs);
        } else {
            sendSuccess(ctx.getSource(), join(join(keys), "other"), otherArgs);
            sendFeedback(ctx.getSource().getServer(), target, join(join(keys), "victim"), victimArgs);
        }
    }

    protected void sendQueryFeedbackWithOptionalTarget(CommandContext<CommandSourceStack> ctx, boolean self, Object[] selfArgs, Object[] otherArgs, String... keys) {
        if (self) {
            sendSuccess(ctx.getSource(), join(join(keys), "self"), selfArgs);
        } else {
            sendSuccess(ctx.getSource(), join(join(keys), "other"), otherArgs);
        }
    }

    protected abstract ArgumentType<T> getArgumentType();

    protected abstract S getArgument(CommandContext<CommandSourceStack> ctx, String string) throws CommandSyntaxException;

    protected abstract S getSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException;

    protected abstract void sendFeedback(MinecraftServer server, S target, String translation, Object... args);

    protected abstract int execute(CommandContext<CommandSourceStack> ctx, S target, boolean self) throws CommandSyntaxException;

}
