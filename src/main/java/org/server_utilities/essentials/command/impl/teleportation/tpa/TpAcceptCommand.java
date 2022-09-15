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
import org.server_utilities.essentials.config.util.WaitingPeriodConfig;
import org.server_utilities.essentials.util.KeyUtil;
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
            sendFailure(ctx.getSource(), "noPending", target.getDisplayName());
            return FAILURE;
        }
        TpaManager.INSTANCE.removeRequest(participants);
        player.sendSystemMessage(Component.translatable(translation("self")));
        target.sendSystemMessage(Component.translatable(translation("victim"), player.getDisplayName()));
        ServerPlayer teleporting = direction == HERE ? player : target;
        ServerPlayer teleportingTarget = direction == HERE ? target : player;
        CompletableFuture<WaitingPeriodConfig.WaitingResult> waitingPeriodFuture = new CompletableFuture<>();
                ScheduleUtil.INSTANCE.scheduleTeleport(teleporting.createCommandSourceStack(), config().tpa.waitingPeriod.cancellation, config().tpa.waitingPeriod.period, seconds -> {
            teleporting.sendSystemMessage(Component.translatable(KeyUtil.translation("teleport", "wait"), seconds));
            teleportingTarget.sendSystemMessage(Component.translatable(KeyUtil.translation("teleport", "wait", "other"), teleporting.getDisplayName(), seconds));
        }, waitingPeriodFuture);
        waitingPeriodFuture.whenCompleteAsync((waitingResult, throwable) -> {
                    if (waitingResult.isCancelled()) {
                        teleporting.sendSystemMessage(Component.translatable(waitingResult.getTranslationKeySelf()));
                        teleportingTarget.sendSystemMessage(Component.translatable(waitingResult.getTranslationKeyOther(), teleporting.getDisplayName()));
                        return;
                    }
                    TeleportationUtil.teleportEntity(teleporting, teleportingTarget.getLevel(), teleportingTarget.getOnPos().above());
                }, ctx.getSource().getServer()
        );
        return SUCCESS;
    }

}
