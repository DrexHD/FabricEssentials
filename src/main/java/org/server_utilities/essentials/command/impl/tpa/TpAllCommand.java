package org.server_utilities.essentials.command.impl.tpa;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.drex.message.api.Message;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.util.TpaManager;

import java.util.HashMap;
import java.util.List;

public class TpAllCommand extends Command {

    public TpAllCommand() {
        super(CommandProperties.create("tpall", 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
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
            target.sendSystemMessage(Message.message("fabric-essentials.commands.tpahere.victim", PlaceholderContext.of(ctx.getSource())));
            success++;
        }
        final int count = success;
        ctx.getSource().sendSuccess(() -> Message.message("fabric-essentials.commands.tpall", new HashMap<>() {{
            put("count", Component.literal(String.valueOf(count)));
        }}), false);
        return success;
    }
}
