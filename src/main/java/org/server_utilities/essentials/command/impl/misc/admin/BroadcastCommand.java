package org.server_utilities.essentials.command.impl.misc.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import me.drex.message.api.Message;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.util.IdentifierUtil;
import org.server_utilities.essentials.util.StyledInputUtil;

import java.util.HashMap;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
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
        ParentTextNode textNode = StyledInputUtil.parseNodes(message, TextParserV1.DEFAULT, textTag -> IdentifierUtil.check(src, "style.broadcast." + textTag.name()));
        src.getServer().getPlayerList().broadcastSystemMessage(Message.message("fabric-essentials.commands.broadcast", new HashMap<>() {{
            put("message", Placeholders.parseText(textNode, PlaceholderContext.of(src)));
        }}), false);
        return 1;
    }
}
