package org.server_utilities.essentials.command.impl.tpa;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.util.TpaManager;

import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.arguments.EntityArgument.getPlayer;
import static net.minecraft.commands.arguments.EntityArgument.player;

public class TpaCommand extends Command {

    private static final String TARGET_ARGUMENT_ID = "target";
    private final TpaManager.Direction direction;

    public static final TpaCommand TPA = new TpaCommand(TpaManager.Direction.THERE);
    public static final TpaCommand TPA_HERE = new TpaCommand(TpaManager.Direction.HERE);

    private TpaCommand(TpaManager.Direction direction) {
        super(direction.getProperties());
        this.direction = direction;
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
        TpaManager.Participants participants = new TpaManager.Participants(ctx.getSource().getPlayerOrException().getUUID(), target.getUUID());
        TpaManager.Direction direction = TpaManager.INSTANCE.getRequest(participants);
        if (direction == this.direction) {
            ctx.getSource().sendFailure(localized("fabric-essentials.commands.tpa.pending", PlaceholderContext.of(target)));
            return FAILURE;
        }
        TpaManager.INSTANCE.addRequest(participants, this.direction);
        ctx.getSource().sendSuccess(() -> localized("fabric-essentials.commands." + this.direction.getTranslationKey() + ".self", PlaceholderContext.of(target)), false);
        target.sendSystemMessage(localized("fabric-essentials.commands." + this.direction.getTranslationKey() + ".victim", PlaceholderContext.of(ctx.getSource())));
        return SUCCESS;
    }

}
