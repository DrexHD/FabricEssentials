package org.server_utilities.essentials.command.impl.teleportation.tpr;

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
import org.server_utilities.essentials.util.TeleportationUtil;
import org.server_utilities.essentials.util.TprManager;

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
        TprManager.Participants participants = new TprManager.Participants(target.getUUID(), player.getUUID());
        TprManager.Direction direction = TprManager.INSTANCE.getRequest(participants);
        if (direction == null) {
            // TODO: sent error
            sendError(ctx, "text.fabric-essentials.command.tpaccept.noPending", target.getDisplayName());
            return 0;
        }
        switch (direction) {
            case HERE -> TeleportationUtil.teleportEntity(player, target.getLevel(), target.getOnPos().above());
            case THERE -> TeleportationUtil.teleportEntity(target, player.getLevel(), player.getOnPos().above());
        }
        sendFeedback(ctx, "text.fabric-essentials.command.tpaccept.self");
        sendFeedback(target, "text.fabric-essentials.command.tpaccept.victim", ctx.getSource().getDisplayName());
        return 1;
    }

}
