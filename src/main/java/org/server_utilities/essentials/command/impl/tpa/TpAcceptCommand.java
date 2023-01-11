package org.server_utilities.essentials.command.impl.tpa;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.drex.message.api.Message;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.command.util.CommandUtil;
import org.server_utilities.essentials.util.AsyncTeleportPlayer;
import org.server_utilities.essentials.util.ComponentPlaceholderUtil;
import org.server_utilities.essentials.util.TeleportCancelException;
import org.server_utilities.essentials.util.TpaManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.arguments.EntityArgument.getPlayer;
import static net.minecraft.commands.arguments.EntityArgument.player;
import static org.server_utilities.essentials.util.TpaManager.Direction.HERE;

public class TpAcceptCommand extends Command {

    private static final String TARGET_ARGUMENT_ID = "target";

    public TpAcceptCommand() {
        super(CommandProperties.create("tpaccept", 0));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.then(
                argument(TARGET_ARGUMENT_ID, player())
                        .executes(this::execute)
        );
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = getPlayer(ctx, TARGET_ARGUMENT_ID);
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        TpaManager.Participants participants = new TpaManager.Participants(target.getUUID(), player.getUUID());
        TpaManager.Direction direction = TpaManager.INSTANCE.getRequest(participants);
        if (direction == null) {
            ctx.getSource().sendFailure(Message.message("fabric-essentials.commands.tpaccept.no_pending", PlaceholderContext.of(target)));
            return FAILURE;
        }
        TpaManager.INSTANCE.removeRequest(participants);
        ctx.getSource().sendSuccess(Message.message("fabric-essentials.commands.tpaccept.self", PlaceholderContext.of(target)), false);
        target.sendSystemMessage(Message.message("fabric-essentials.commands.tpaccept.victim", PlaceholderContext.of(ctx.getSource())), false);
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
                    teleportingTargetSource.sendFailure(Message.message("fabric-essentials.teleport.cancel.other", PlaceholderContext.of(teleporting)));
                } else {
                    teleportingSource.sendFailure(Message.message("fabric-essentials.teleport.wait.error", ComponentPlaceholderUtil.exceptionPlaceholders(completionException)));
                    teleportingTargetSource.sendFailure(Message.message("fabric-essentials.teleport.wait.error", ComponentPlaceholderUtil.exceptionPlaceholders(completionException)));
                    LOGGER.error("An unknown error occurred, during waiting period", completionException.getCause());
                }
            } else {
                CommandUtil.teleportEntity(teleporting, teleportingTarget.getLevel(), teleportingTarget.getOnPos().above());
            }
        }, ctx.getSource().getServer());
        return SUCCESS;
    }

}
