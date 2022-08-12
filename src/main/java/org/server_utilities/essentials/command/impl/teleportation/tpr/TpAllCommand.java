package org.server_utilities.essentials.command.impl.teleportation.tpr;

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
import org.server_utilities.essentials.util.TprManager;

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
        for (ServerPlayer target : players) {
            TprManager.Participants participants = new TprManager.Participants(ctx.getSource().getPlayerOrException().getUUID(), target.getUUID());
            TprManager.Direction direction = TprManager.INSTANCE.getRequest(participants);
            if (direction == TprManager.Direction.HERE) {
                sendError(ctx, "text.fabric-essentials.command.tpa.pending");
                return 0;
            }
            TprManager.INSTANCE.addRequest(participants, TprManager.Direction.HERE);
            sendFeedback(target, "text.fabric-essentials.command.tpa.%s.victim".formatted(TprManager.Direction.HERE.getTranslationKey()),
                    ctx.getSource().getDisplayName(),
                    Component.translatable("text.fabric-essentials.command.tpa.accept")
                            .withStyle(style ->
                                    style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept %s".formatted(ctx.getSource().getPlayer().getScoreboardName())))
                                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("text.fabric-essentials.command.tpa.accept.hover")))
                            )
            );
        }
        // TODO: sent feedback to source
        //sendFeedback(ctx, "text.fabric-essentials.command.tpa.%s.self".formatted(TprManager.Direction.HERE.getTranslationKey()), target.getDisplayName());
        return players.size();
    }
}
