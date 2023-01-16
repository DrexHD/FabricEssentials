package org.server_utilities.essentials.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.drex.message.api.Message;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.arguments.EntityArgument.getPlayer;
import static net.minecraft.commands.arguments.EntityArgument.player;

public abstract class SimpleTargetCommand extends Command {

    private final String messageId;

    public SimpleTargetCommand(@NotNull CommandProperties commandProperties, String messageId) {
        super(commandProperties);
        this.messageId = messageId;
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
                argument("player", player())
                        .requires(require("other"))
                        .executes(ctx -> {
                            ServerPlayer target = getPlayer(ctx, "player");
                            ctx.getSource().sendSuccess(Message.message(messageId + ".other", PlaceholderContext.of(target)), false);
                            return execute(target);
                        })
        ).executes(ctx -> {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            ctx.getSource().sendSuccess(Message.message(messageId + ".self"), false);
            return execute(player);
        });
    }

    protected abstract int execute(ServerPlayer target);

}
