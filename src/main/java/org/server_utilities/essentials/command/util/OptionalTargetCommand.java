package org.server_utilities.essentials.command.util;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;

public abstract class OptionalTargetCommand<T, S> extends Command {

    private String targetArgumentId = "target";

    public OptionalTargetCommand(Properties properties) {
        super(properties);
    }

    public OptionalTargetCommand(Properties properties, String targetArgumentId) {
        super(properties);
        this.targetArgumentId = targetArgumentId;
    }

    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        registerOptionalArgument(literal);
    }

    protected void registerOptionalArgument(ArgumentBuilder<CommandSourceStack, ?> argument) {
        argument.executes(this::execute);
        RequiredArgumentBuilder<CommandSourceStack, T> target = Commands.argument(targetArgumentId, getArgumentType());
        String permission = this.properties.getPermission();
        if (permission != null) target.requires(permission(permission, OTHER_PERMISSION_SUFFIX));
        target.executes(this::executeOther);
        argument.then(target);
    }

    protected abstract ArgumentType<T> getArgumentType();

    protected abstract S getArgument(CommandContext<CommandSourceStack> ctx, String string) throws CommandSyntaxException;

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer sender = ctx.getSource().getPlayerOrException();
        return onSelf(ctx, sender);
    }

    private int executeOther(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer sender = ctx.getSource().getPlayerOrException();
        S target = getArgument(ctx, targetArgumentId);
        return onOther(ctx, sender, target);
    }

    protected abstract int onSelf(CommandContext<CommandSourceStack> ctx, ServerPlayer sender) throws CommandSyntaxException;

    protected abstract int onOther(CommandContext<CommandSourceStack> ctx, ServerPlayer sender, S target) throws CommandSyntaxException;

}
