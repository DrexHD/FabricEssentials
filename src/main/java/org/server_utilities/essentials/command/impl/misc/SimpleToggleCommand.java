package org.server_utilities.essentials.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;

import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.arguments.EntityArgument.getPlayer;
import static net.minecraft.commands.arguments.EntityArgument.player;

public abstract class SimpleToggleCommand extends Command {

    private final String messageId;

    public SimpleToggleCommand(@NotNull CommandProperties commandProperties, String messageId) {
        super(commandProperties);
        this.messageId = messageId;
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext context) {
        literal.then(
                argument("player", player())
                        .requires(require("other"))
                        .executes(ctx -> {
                            ServerPlayer target = getPlayer(ctx, "player");
                            boolean previousState = getState(target);
                            ctx.getSource().sendSuccess(() -> localized(messageId + (previousState ? ".disable" : ".enable") + ".other", PlaceholderContext.of(target)), false);
                            setState(target, !previousState);
                            return previousState ? FAILURE : SUCCESS;
                        })

        ).executes(ctx -> {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            boolean previousState = getState(player);
            ctx.getSource().sendSuccess(() -> localized(messageId + (previousState ? ".disable" : ".enable") + ".self"), false);
            setState(player, !previousState);
            return previousState ? FAILURE : SUCCESS;
        });
    }

    protected abstract boolean getState(ServerPlayer target);

    protected abstract void setState(ServerPlayer target, boolean state);

}
