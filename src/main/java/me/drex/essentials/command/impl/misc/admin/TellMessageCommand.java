package me.drex.essentials.command.impl.misc.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.util.StyledInputUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.arguments.EntityArgument.getPlayers;
import static net.minecraft.commands.arguments.EntityArgument.players;

public class TellMessageCommand extends Command {
    private static final NodeParser PARSER = NodeParser.builder()
        .serverPlaceholders()
        .build();

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
        TextNode textNode = StyledInputUtil.parseNode(message, src, "style.tellmessage.", false);
        for (ServerPlayer player : players) {
            Component component = PARSER.parseComponent(textNode, ServerPlaceholderContext.of(player).asParserContext());
            player.sendSystemMessage(component);
        }
        return players.size();
    }

}
