package org.server_utilities.essentials.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.drex.message.api.Message;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.arguments.EntityArgument.getPlayer;
import static net.minecraft.commands.arguments.EntityArgument.player;

public class PingCommand extends Command {

    public PingCommand() {
        super(CommandProperties.create("ping", 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
                argument("player", player())
                        .requires(require("other"))
                        .executes(ctx -> {
                            ServerPlayer player = getPlayer(ctx, "player");
                            ctx.getSource().sendSystemMessage(Message.message("fabric-essentials.commands.ping.self"));
                            return player.connection.latency();
                        })
        ).executes(ctx -> {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            ctx.getSource().sendSystemMessage(Message.message("fabric-essentials.commands.ping.other", PlaceholderContext.of(player)));
            return player.connection.latency();
        });
    }
}
