package org.server_utilities.essentials.command.impl.teleportation.tpa;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.util.TprManager;

// TODO:
public class TpaCommand extends Command {

    private static final String TARGET_ARGUMENT_ID = "target";
    private final TprManager.Direction direction;

    public TpaCommand(TprManager.Direction direction) {
        super(direction.getProperties());
        this.direction = direction;
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        RequiredArgumentBuilder<CommandSourceStack, EntitySelector> target = Commands.argument(TARGET_ARGUMENT_ID, EntityArgument.player());
        target.executes(this::execute);
        literal.then(target);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, TARGET_ARGUMENT_ID);
        TprManager.Participants participants = new TprManager.Participants(ctx.getSource().getPlayerOrException().getUUID(), target.getUUID());
        TprManager.Direction direction = TprManager.INSTANCE.getRequest(participants);
        if (direction == this.direction) {
            sendError(ctx, "text.fabric-essentials.command.tpa.pending");
            return 0;
        }
        TprManager.INSTANCE.addRequest(participants, this.direction);
        sendFeedback(ctx, "text.fabric-essentials.command.tpa.%s.self".formatted(this.direction.getTranslationKey()), target.getDisplayName());
        sendFeedback(target, "text.fabric-essentials.command.tpa.%s.victim".formatted(this.direction.getTranslationKey()),
                ctx.getSource().getDisplayName(),
                Component.translatable("text.fabric-essentials.command.tpa.accept")
                        .withStyle(style ->
                                style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept %s".formatted(ctx.getSource().getPlayer().getScoreboardName())))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("text.fabric-essentials.command.tpa.accept.hover")))
                        )
        );
        return 1;
    }

}
