package me.drex.essentials.command.impl.misc.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
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
            Component component = Placeholders.parseText(textNode, PlaceholderContext.of(player));
            player.sendSystemMessage(component);
        }
        return players.size();
    }

}
