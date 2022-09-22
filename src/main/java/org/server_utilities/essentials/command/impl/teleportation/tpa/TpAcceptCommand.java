package org.server_utilities.essentials.command.impl.teleportation.tpa;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.util.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.server_utilities.essentials.util.TpaManager.Direction.HERE;

public class TpAcceptCommand extends Command {

    private static final String TARGET_ARGUMENT_ID = "target";

    public TpAcceptCommand() {
        super(Properties.create("tpaccept"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        RequiredArgumentBuilder<CommandSourceStack, EntitySelector> target = Commands.argument(TARGET_ARGUMENT_ID, EntityArgument.player());
        target.executes(this::execute);
        literal.then(target);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, TARGET_ARGUMENT_ID);
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        TpaManager.Participants participants = new TpaManager.Participants(target.getUUID(), player.getUUID());
        TpaManager.Direction direction = TpaManager.INSTANCE.getRequest(participants);
        if (direction == null) {
            sendFailure(ctx.getSource(), "noPending", target.getDisplayName());
            return FAILURE;
        }
        TpaManager.INSTANCE.removeRequest(participants);
        sendSuccess(ctx.getSource(), "self");
        sendSuccess(target.createCommandSourceStack(), "victim", player.getDisplayName());
        ServerPlayer teleporting = direction == HERE ? player : target;
        ServerPlayer teleportingTarget = direction == HERE ? target : player;
        CommandSourceStack teleportingSource = teleporting.createCommandSourceStack();
        CommandSourceStack teleportingTargetSource = teleportingTarget.createCommandSourceStack();
        CompletableFuture.allOf(
                ((AsyncTeleportPlayer) teleporting).delayedTeleport(teleportingSource, config().tpa.waitingPeriod),
                ((AsyncTeleportPlayer) teleportingTarget).delayedTeleport(teleportingTargetSource, config().tpa.waitingPeriod.period * 20, null)
        ).whenCompleteAsync((unused, throwable) -> {
            if (throwable instanceof CompletionException completionException) {
                if (completionException.getCause() instanceof TeleportCancelException exception) {
                    teleportingSource.sendFailure(exception.getRawMessage());
                    teleportingTargetSource.sendFailure(Component.translatable(KeyUtil.translation("teleport.cancel.other"), teleporting.getDisplayName()));
                } else {
                    teleportingSource.sendFailure(Component.translatable(KeyUtil.translation("teleport.wait.error")));
                    teleportingTargetSource.sendFailure(Component.translatable(KeyUtil.translation("teleport.wait.error")));
                    LOGGER.error("An unknown error occurred, during waiting period", completionException.getCause());
                }
            } else {
                TeleportationUtil.teleportEntity(teleporting, teleportingTarget.getLevel(), teleportingTarget.getOnPos().above());
            }
        }, ctx.getSource().getServer());
        return SUCCESS;
    }

}
