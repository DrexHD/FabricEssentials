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

public class InvulnerableCommand extends OptionalOnlineTargetCommand {

    private static final String ENABLE = "enable";

    public InvulnerableCommand() {
        super(Properties.create("invulnerable", "godmode"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        RequiredArgumentBuilder<CommandSourceStack, Boolean> enable = Commands.argument("enable", BoolArgumentType.bool());
        registerOptionalArgument(enable);
        literal.then(enable);
        literal.executes(this::toggle);
    }

    @Override
    protected int execute(CommandContext<CommandSourceStack> ctx, ServerPlayer target, boolean self) throws CommandSyntaxException {
        return setInvulnerable(ctx, target, BoolArgumentType.getBool(ctx, ENABLE), self);
    }

    private int toggle(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer serverPlayer = ctx.getSource().getPlayerOrException();
        return setInvulnerable(ctx, serverPlayer, !serverPlayer.isInvulnerable(), true);
    }

    private int setInvulnerable(CommandContext<CommandSourceStack> ctx, ServerPlayer target, boolean value, boolean self) {
        sendFeedbackWithOptionalTarget(ctx, target, self, value ? "fabric-essentials.commands.invulnerable.enable" : "fabric-essentials.commands.invulnerable.disable");
        target.setInvulnerable(value);
        target.onUpdateAbilities();
        return value ? SUCCESS : FAILURE;
    }

}
