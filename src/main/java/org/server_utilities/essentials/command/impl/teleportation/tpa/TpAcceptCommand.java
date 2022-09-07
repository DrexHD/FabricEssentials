package org.server_utilities.essentials.command.impl.teleportation.tpa;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.config.util.WaitingPeriodConfig;
import org.server_utilities.essentials.util.ScheduleUtil;
import org.server_utilities.essentials.util.TeleportationUtil;
import org.server_utilities.essentials.util.TpaManager;

import java.util.concurrent.CompletableFuture;

import static org.server_utilities.essentials.util.TpaManager.Direction.HERE;

public class TpAcceptCommand extends Command {

    private static final String TARGET_ARGUMENT_ID = "target";

    public TpAcceptCommand() {
        super(Properties.create("tpaccept").permission("tpaccept"));
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
            sendError(ctx, "text.fabric-essentials.command.tpaccept.noPending", target.getDisplayName());
            return 0;
        }
        TpaManager.INSTANCE.removeRequest(participants);
        ServerPlayer teleporting = direction == HERE ? player : target;
        ServerPlayer teleportingTarget = direction == HERE ? target : player;
        CompletableFuture<WaitingPeriodConfig.WaitingResult> waitingPeriodFuture = new CompletableFuture<>();
                ScheduleUtil.INSTANCE.scheduleTeleport(teleporting.createCommandSourceStack(), config().tpa.waitingPeriod.cancellation, config().tpa.waitingPeriod.period, seconds -> {
            sendFeedback(teleporting, "text.fabric-essentials.teleport.wait", seconds);
            sendFeedback(teleportingTarget, "text.fabric-essentials.teleport.wait.other", teleporting.getDisplayName(), seconds);
        }, waitingPeriodFuture);
        waitingPeriodFuture.whenCompleteAsync((waitingResult, throwable) -> {
                    if (waitingResult.isCancelled()) {
                        sendError(teleporting, waitingResult.getTranslationKeySelf());
                        sendError(teleportingTarget, waitingResult.getTranslationKeyOther(), teleporting.getDisplayName());
                        return;
                    }
                    TeleportationUtil.teleportEntity(teleporting, teleportingTarget.getLevel(), teleportingTarget.getOnPos().above());
                    sendFeedback(player, "text.fabric-essentials.command.tpaccept.self");
                    sendFeedback(target, "text.fabric-essentials.command.tpaccept.victim", player.getDisplayName());
                }, ctx.getSource().getServer()
        );
        return 1;
    }

}
