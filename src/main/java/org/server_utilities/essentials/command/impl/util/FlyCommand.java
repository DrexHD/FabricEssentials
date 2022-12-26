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
        super(Properties.create("fly"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        RequiredArgumentBuilder<CommandSourceStack, Boolean> enable = Commands.argument(ENABLE, BoolArgumentType.bool());
        registerOptionalArgument(enable);
        literal.then(enable);
        literal.executes(this::toggle);
    }

    @Override
    protected int execute(CommandContext<CommandSourceStack> ctx, ServerPlayer target, boolean self) throws CommandSyntaxException {
        return setFly(ctx, target, BoolArgumentType.getBool(ctx, ENABLE), self);
    }

    private int toggle(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer serverPlayer = ctx.getSource().getPlayerOrException();
        return setFly(ctx, serverPlayer, !serverPlayer.getAbilities().mayfly, true);
    }

    private int setFly(CommandContext<CommandSourceStack> ctx, ServerPlayer target, boolean value, boolean self) {
        sendFeedbackWithOptionalTarget(ctx, target, self, value ? "fabric-essentials.commands.fly.enable" : "fabric-essentials.commands.fly.disable");
        target.getAbilities().mayfly = value;
        if (!value) target.getAbilities().flying = false;
        target.onUpdateAbilities();
        return value ? SUCCESS : FAILURE;
    }
}
