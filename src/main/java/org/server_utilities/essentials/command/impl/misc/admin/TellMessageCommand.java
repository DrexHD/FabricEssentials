package org.server_utilities.essentials.command.impl.misc.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.util.IdentifierUtil;
import org.server_utilities.essentials.util.StyledInputUtil;

import java.util.Collection;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.arguments.EntityArgument.getPlayers;
import static net.minecraft.commands.arguments.EntityArgument.players;

public class TellMessageCommand extends Command {

    public TellMessageCommand() {
        super(CommandProperties.create("tellmessage", 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
                argument("targets", players()).then(
                        argument("message", greedyString())
                                .executes(ctx -> tellMessage(
                                        ctx.getSource(),
                                        getPlayers(ctx, "targets"),
                                        getString(ctx, "message")
                                ))
                )
        );
    }

    protected int tellMessage(CommandSourceStack src, Collection<ServerPlayer> players, String message) {
        ParentTextNode textNode = StyledInputUtil.parseNodes(message, TextParserV1.DEFAULT, textTag -> IdentifierUtil.check(src, "style.tellmessage." + textTag.name()));
        for (ServerPlayer player : players) {
            Component component = Placeholders.parseText(textNode, PlaceholderContext.of(player));
            player.sendSystemMessage(component);
        }
        return players.size();
    }

}
