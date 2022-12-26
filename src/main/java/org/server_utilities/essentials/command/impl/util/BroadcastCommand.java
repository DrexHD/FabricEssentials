package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.parent.ParentTextNode;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import me.drex.message.api.Message;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.util.KeyUtil;
import org.server_utilities.essentials.util.StyledInputUtil;

import java.util.HashMap;

public class BroadcastCommand extends Command {

    public BroadcastCommand() {
        super(Properties.create("broadcast"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.then(
                Commands.argument("message", StringArgumentType.greedyString())
                        .executes(ctx -> tellMessage(
                                ctx.getSource(),
                                StringArgumentType.getString(ctx, "message")
                        ))
        );
    }

    public int tellMessage(CommandSourceStack src, String message) {
        ParentTextNode textNode = StyledInputUtil.parseNodes(message, TextParserV1.DEFAULT, textTag -> KeyUtil.permission(src, "style.broadcast", textTag.name()));
        src.getServer().getPlayerList().broadcastSystemMessage(Message.message("fabric-essentials.commands.broadcast", new HashMap<>() {{
           put("message", Placeholders.parseText(textNode, PlaceholderContext.of(src)));
        }}), false);
        return 1;
    }
}
