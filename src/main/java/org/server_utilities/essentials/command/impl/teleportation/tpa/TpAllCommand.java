package org.server_utilities.essentials.command.impl.teleportation.tpa;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.util.TpaManager;

import java.util.List;

public class TpAllCommand extends Command {

    public TpAllCommand() {
        super(Properties.create("tpall").permission("tpall"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.executes(this::execute);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        List<ServerPlayer> players = ctx.getSource().getServer().getPlayerList().getPlayers();
        int success = 0;
        for (ServerPlayer target : players) {
            if (ctx.getSource().getPlayerOrException().equals(target)) continue;
            TpaManager.Participants participants = new TpaManager.Participants(ctx.getSource().getPlayerOrException().getUUID(), target.getUUID());
            TpaManager.Direction direction = TpaManager.INSTANCE.getRequest(participants);
            if (direction == TpaManager.Direction.HERE) continue;
            TpaManager.INSTANCE.addRequest(participants, TpaManager.Direction.HERE);
            target.sendSystemMessage(Component.translatable(translation(TpaManager.Direction.HERE.getTranslationKey(), "victim"), ctx.getSource().getDisplayName(),
                    Component.translatable(translation("accept"))
                            .withStyle(style ->
                                    style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept %s".formatted(ctx.getSource().getPlayer().getScoreboardName())))
                                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(translation("accept", "hover"))))
                            )));
            success++;
        }
        sendSuccess(ctx.getSource(), null, success);
        return success;
    }
}
