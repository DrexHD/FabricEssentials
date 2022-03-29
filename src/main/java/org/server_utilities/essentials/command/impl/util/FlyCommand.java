package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOnlineTargetCommand;

public class FlyCommand extends OptionalOnlineTargetCommand {

    private static final String ENABLE = "enable";

    public FlyCommand() {
        super(Properties.create("fly").permission("fly"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        RequiredArgumentBuilder<CommandSourceStack, Boolean> enable = Commands.argument("enable", BoolArgumentType.bool());
        registerOptionalArgument(enable);
        literal.then(enable);
        literal.executes(this::toggle);
    }

    @Override
    protected int onSelf(CommandContext<CommandSourceStack> ctx, ServerPlayer sender) throws CommandSyntaxException {
        return setFly(ctx, sender, BoolArgumentType.getBool(ctx, ENABLE), true);
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, ServerPlayer sender, ServerPlayer target) throws CommandSyntaxException {
        return setFly(ctx, target, BoolArgumentType.getBool(ctx, ENABLE), false);
    }

    private int toggle(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer serverPlayer = ctx.getSource().getPlayerOrException();
        return setFly(ctx, serverPlayer, !serverPlayer.getAbilities().mayfly, true);
    }

    private int setFly(CommandContext<CommandSourceStack> ctx, ServerPlayer target, boolean value, boolean self) {
        sendFeedback(ctx,
                String.format("text.fabric-essentials.command.fly.%s", self ? "self" : "other"),
                self ? new Object[]{value} : new Object[]{value, target.getDisplayName()}
        );
        if (!self) sendFeedback(target,
                String.format("text.fabric-essentials.command.fly.%s", "victim"),
                value, toName(ctx)
        );
        target.getAbilities().mayfly = value;
        if (!value) target.getAbilities().flying = false;
        target.onUpdateAbilities();
        return value ? 1 : 0;
    }
}
