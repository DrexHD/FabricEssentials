package me.drex.essentials.command.impl.tpa;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import me.drex.essentials.command.Command;
import me.drex.essentials.util.TpaManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.network.chat.Component;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;

import java.util.List;

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
                argument(TARGET_ARGUMENT_ID, StringArgumentType.word())
                        .suggests((context, builder) -> {
                            String input = builder.getRemaining().toLowerCase();
                            context.getSource().getServer().getPlayerList().getPlayers().stream()
                                .map(player -> player.getGameProfile().getName())
                                .filter(name -> name.toLowerCase().startsWith(input))
                                .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(this::execute)
        );
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String partialName = StringArgumentType.getString(ctx, TARGET_ARGUMENT_ID);
        ServerPlayer target = findPlayerByPartialName(ctx.getSource(), partialName);
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

    protected static ServerPlayer findPlayerByPartialName(CommandSourceStack source, String partialName) throws CommandSyntaxException {
        String lowercasePartial = partialName.toLowerCase();
        List<ServerPlayer> matches = source.getServer().getPlayerList().getPlayers().stream()
                .filter(player -> player.getGameProfile().getName().toLowerCase().startsWith(lowercasePartial))
                .toList();

        if (matches.isEmpty()) {
            throw new SimpleCommandExceptionType(
                Component.literal("No player found matching '" + partialName + "'"))
                .create();
        }
        if (matches.size() > 1) {
            throw new SimpleCommandExceptionType(
                Component.literal("Multiple players found matching '" + partialName + "'"))
                .create();
        }
        return matches.getFirst();
    }

}
