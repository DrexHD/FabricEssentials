package me.drex.essentials.command.impl.misc.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
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
    private static final NodeParser PARSER = NodeParser.builder()
        .serverPlaceholders()
        .build();

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
            put("message", PARSER.parseComponent(textNode, ServerPlaceholderContext.of(src).asParserContext()));
        }}), false);
        return 1;
    }
}
