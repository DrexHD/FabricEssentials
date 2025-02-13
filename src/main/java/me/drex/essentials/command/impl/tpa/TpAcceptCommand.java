package me.drex.essentials.command.impl.tpa;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.util.AsyncTeleportPlayer;
import me.drex.essentials.util.ComponentPlaceholderUtil;
import me.drex.essentials.util.TeleportCancelException;
import me.drex.essentials.util.TpaManager;
import me.drex.essentials.util.teleportation.Location;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.List;

import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;
import static me.drex.essentials.util.TpaManager.Direction.HERE;

public class TpAcceptCommand extends Command {

    private static final String TARGET_ARGUMENT_ID = "target";

    public TpAcceptCommand() {
        super(CommandProperties.create("tpaccept", 0));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.executes(this::executeNoArg)
        .then(
                argument(TARGET_ARGUMENT_ID, EntityArgument.player())
                        .executes(ctx -> {
                            ServerPlayer target = EntityArgument.getPlayer(ctx, TARGET_ARGUMENT_ID);
                            return execute(ctx, target);
                        })
        );
    }

    private int executeNoArg(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        List<TpaManager.Participants> requests = TpaManager.INSTANCE.getRequestsFor(player.getUUID());
        
        if (requests.isEmpty()) {
            ctx.getSource().sendFailure(localized("fabric-essentials.commands.tpaccept.no_requests"));
            return FAILURE;
        }
        
        if (requests.size() > 1) {
            ctx.getSource().sendFailure(localized("fabric-essentials.commands.tpaccept.multiple_requests"));
            return FAILURE;
        }
        
        TpaManager.Participants participants = requests.getFirst();
        ServerPlayer target = ctx.getSource().getServer().getPlayerList().getPlayer(participants.requester());
        if (target == null) {
            ctx.getSource().sendFailure(localized("fabric-essentials.commands.tpaccept.player_offline"));
            return FAILURE;
        }
        
        return execute(ctx, target);
    }

    private int execute(CommandContext<CommandSourceStack> ctx, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        TpaManager.Participants participants = new TpaManager.Participants(target.getUUID(), player.getUUID());
        TpaManager.Direction direction = TpaManager.INSTANCE.getRequest(participants);
        if (direction == null) {
            ctx.getSource().sendFailure(localized("fabric-essentials.commands.tpaccept.no_pending", PlaceholderContext.of(target)));
            return FAILURE;
        }
        TpaManager.INSTANCE.removeRequest(participants);
        ctx.getSource().sendSuccess(() -> localized("fabric-essentials.commands.tpaccept.self", PlaceholderContext.of(target)), false);
        target.sendSystemMessage(localized("fabric-essentials.commands.tpaccept.victim", PlaceholderContext.of(ctx.getSource())), false);
        ServerPlayer teleporting = direction == HERE ? player : target;
        ServerPlayer teleportingTarget = direction == HERE ? target : player;
        CommandSourceStack teleportingSource = teleporting.createCommandSourceStack();
        CommandSourceStack teleportingTargetSource = teleportingTarget.createCommandSourceStack();
        CompletableFuture.allOf(
                ((AsyncTeleportPlayer) teleporting).delayedTeleport(teleportingSource, config().teleportation.waitingPeriod),
                ((AsyncTeleportPlayer) teleportingTarget).delayedTeleport(teleportingTargetSource, config().teleportation.waitingPeriod.period * 20, null)
        ).whenCompleteAsync((unused, throwable) -> {
            if (throwable instanceof CompletionException completionException) {
                if (completionException.getCause() instanceof TeleportCancelException exception) {
                    teleportingSource.sendFailure(exception.getRawMessage());
                    teleportingTargetSource.sendFailure(localized("fabric-essentials.teleport.cancel.other", PlaceholderContext.of(teleporting)));
                } else {
                    teleportingSource.sendFailure(localized("fabric-essentials.teleport.wait.error", ComponentPlaceholderUtil.exceptionPlaceholders(completionException)));
                    teleportingTargetSource.sendFailure(localized("fabric-essentials.teleport.wait.error", ComponentPlaceholderUtil.exceptionPlaceholders(completionException)));
                    LOGGER.error("An unknown error occurred, during waiting period", completionException.getCause());
                }
            } else {
                Location location = new Location(teleportingTarget);
                location.teleport(teleporting);
            }
        }, ctx.getSource().getServer());
        return SUCCESS;
    }

}
