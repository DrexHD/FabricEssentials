package org.server_utilities.essentials.command.util;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;

public abstract class OptionalTargetCommand<T, S> extends Command {

    private final String targetArgumentId;

    public OptionalTargetCommand(Properties properties) {
        this(properties, "target");
    }

    public OptionalTargetCommand(Properties properties, String targetArgumentId) {
        super(properties);
        // Allow literal command execution if either of the sub-nodes is present
        String permission = properties.permission();
        if (permission != null) {
            properties
                    .orPredicate(permission(permission, SELF_PERMISSION_SUFFIX))
                    .orPredicate(permission(permission, OTHER_PERMISSION_SUFFIX));
        }
        this.targetArgumentId = targetArgumentId;
    }

    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        registerOptionalArgument(literal);
    }

    protected void registerOptionalArgument(ArgumentBuilder<CommandSourceStack, ?> argument) {
        argument.executes(this::execute);
        RequiredArgumentBuilder<CommandSourceStack, T> target = Commands.argument(targetArgumentId, getArgumentType());
        String permission = this.properties.permission();
        if (permission != null) {
            argument.requires(permission(permission, SELF_PERMISSION_SUFFIX));
            target.requires(permission(permission, OTHER_PERMISSION_SUFFIX));
        }
        target.executes(this::executeOther);
        argument.then(target);
    }

    protected abstract ArgumentType<T> getArgumentType();

    protected abstract S getArgument(CommandContext<CommandSourceStack> ctx, String string) throws CommandSyntaxException;

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return onSelf(ctx);
    }

    private int executeOther(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return onOther(ctx, getArgument(ctx, targetArgumentId));
    }

    protected abstract int onSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException;

    protected abstract int onOther(CommandContext<CommandSourceStack> ctx, S target) throws CommandSyntaxException;

}
