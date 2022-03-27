package org.server_utilities.essentials.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.server.level.ServerPlayer;

public abstract class SelfAndTargetCommand extends Command {

    private String targetArgumentId = "target";

    public SelfAndTargetCommand(Properties properties) {
        super(properties);
    }

    public SelfAndTargetCommand(Properties properties, String targetArgumentId) {
        super(properties);
        this.targetArgumentId = targetArgumentId;
    }

    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.executes(this::execute);
        RequiredArgumentBuilder<CommandSourceStack, EntitySelector> target = Commands.argument(targetArgumentId, EntityArgument.player());
        target.executes(this::executeOther);
        literal.then(target);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer sender = ctx.getSource().getPlayerOrException();
        return onSelf(ctx, sender);
    }

    private int executeOther(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer sender = ctx.getSource().getPlayerOrException();
        ServerPlayer target = EntityArgument.getPlayer(ctx, targetArgumentId);
        return onOther(ctx, sender, target);
    }

    protected abstract int onSelf(CommandContext<CommandSourceStack> ctx, ServerPlayer sender);

    protected abstract int onOther(CommandContext<CommandSourceStack> ctx, ServerPlayer sender, ServerPlayer target);

}
