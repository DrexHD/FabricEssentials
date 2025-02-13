package me.drex.essentials.command.impl.tpa;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.util.TpaManager;

import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;

import java.util.List;

public class TpDenyCommand extends Command {

    private static final String TARGET_ARGUMENT_ID = "target";

    public TpDenyCommand() {
        super(CommandProperties.create("tpdeny", 0));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.executes(this::executeNoArg)
        .then(
                argument(TARGET_ARGUMENT_ID, EntityArgument.player())
                        .executes(ctx -> {
                            ServerPlayer target = EntityArgument.getPlayer(ctx, TARGET_ARGUMENT_ID);
                            return execute(ctx, target);
                        })
        );
    }

    private int executeNoArg(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        List<TpaManager.Participants> requests = TpaManager.INSTANCE.getRequestsFor(player.getUUID());
        
        if (requests.isEmpty()) {
            ctx.getSource().sendFailure(localized("fabric-essentials.commands.tpdeny.no_requests"));
            return FAILURE;
        }
        
        if (requests.size() > 1) {
            ctx.getSource().sendFailure(localized("fabric-essentials.commands.tpdeny.multiple_requests"));
            return FAILURE;
        }
        
        TpaManager.Participants participants = requests.get(0);
        ServerPlayer target = ctx.getSource().getServer().getPlayerList().getPlayer(participants.requester());
        if (target == null) {
            ctx.getSource().sendFailure(localized("fabric-essentials.commands.tpdeny.player_offline"));
            return FAILURE;
        }
        
        return execute(ctx, target);
    }
    
    private int execute(CommandContext<CommandSourceStack> ctx, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        TpaManager.Participants participants = new TpaManager.Participants(target.getUUID(), player.getUUID());
        TpaManager.Direction direction = TpaManager.INSTANCE.getRequest(participants);
        if (direction == null) {
            ctx.getSource().sendFailure(localized("fabric-essentials.commands.tpdeny.no_pending", PlaceholderContext.of(target)));
            return FAILURE;
        }
        TpaManager.INSTANCE.removeRequest(participants);
        ctx.getSource().sendSuccess(() -> localized("fabric-essentials.commands.tpdeny.self", PlaceholderContext.of(target)), false);
        target.sendSystemMessage(localized("fabric-essentials.commands.tpdeny.victim", PlaceholderContext.of(ctx.getSource())), false);
        return SUCCESS;
    }

}
