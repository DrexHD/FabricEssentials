package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.util.KeyUtil;
import org.server_utilities.essentials.util.StyledInputUtil;

import java.util.Collection;

public class TellMessageCommand extends Command {

    public TellMessageCommand() {
        super(Properties.create("tellmessage"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.then(
                Commands.argument("targets", EntityArgument.players()).then(
                        Commands.argument("message", StringArgumentType.greedyString())
                                .executes(ctx -> tellMessage(
                                        ctx.getSource(),
                                        EntityArgument.getPlayers(ctx, "targets"),
                                        StringArgumentType.getString(ctx, "message")
                                ))
                )
        );
    }

    protected int tellMessage(CommandSourceStack src, Collection<ServerPlayer> players, String message) {
        ParentTextNode textNode = StyledInputUtil.parseNodes(message, textTag -> KeyUtil.permission(src, "style.tellmessage", textTag.name()));
        for (ServerPlayer player : players) {
            Component component = Placeholders.parseText(textNode, PlaceholderContext.of(player));
            player.sendSystemMessage(component);
        }
        return players.size();
    }

}
