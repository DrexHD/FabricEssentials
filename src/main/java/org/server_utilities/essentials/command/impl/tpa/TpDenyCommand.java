package org.server_utilities.essentials.command.impl.tpa;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.drex.message.api.Message;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.util.TpaManager;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.arguments.EntityArgument.getPlayer;
import static net.minecraft.commands.arguments.EntityArgument.player;

public class TpDenyCommand extends Command {

    private static final String TARGET_ARGUMENT_ID = "target";

    public TpDenyCommand() {
        super(CommandProperties.create("tpdeny", 0));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
                argument(TARGET_ARGUMENT_ID, player())
                        .executes(this::execute)
        );
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = getPlayer(ctx, TARGET_ARGUMENT_ID);
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        TpaManager.Participants participants = new TpaManager.Participants(target.getUUID(), player.getUUID());
        TpaManager.Direction direction = TpaManager.INSTANCE.getRequest(participants);
        if (direction == null) {
            ctx.getSource().sendFailure(Message.message("fabric-essentials.commands.tpdeny.no_pending", PlaceholderContext.of(target)));
            return FAILURE;
        }
        TpaManager.INSTANCE.removeRequest(participants);
        ctx.getSource().sendSuccess(Message.message("fabric-essentials.commands.tpdeny.self", PlaceholderContext.of(target)), false);
        target.sendSystemMessage(Message.message("fabric-essentials.commands.tpdeny.victim", PlaceholderContext.of(ctx.getSource())), false);
        return SUCCESS;
    }

}
