package me.drex.essentials.command.impl.misc.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.util.StyledInputUtil;

import java.util.HashMap;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;

public class BroadcastCommand extends Command {

    public BroadcastCommand() {
        super(CommandProperties.create("broadcast", 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
                argument("message", greedyString())
                        .executes(ctx -> tellMessage(
                                ctx.getSource(),
                                getString(ctx, "message")
                        ))
        );
    }

    public int tellMessage(CommandSourceStack src, String message) {
        TextNode textNode = StyledInputUtil.parseNode(message, src, "style.broadcast.", false);
        src.getServer().getPlayerList().broadcastSystemMessage(localized("fabric-essentials.commands.broadcast", new HashMap<>() {{
            put("message", Placeholders.parseText(textNode, PlaceholderContext.of(src)));
        }}), false);
        return 1;
    }
}
