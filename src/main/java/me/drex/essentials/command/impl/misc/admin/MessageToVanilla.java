package me.drex.essentials.command.impl.misc.admin;

import com.google.gson.JsonElement;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.JsonOps;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.parsers.NodeParser;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;
import me.drex.message.api.LocalizedMessage;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class MessageToVanilla extends Command {

    private static final NodeParser QUICKTEXT = NodeParser.builder().quickText().build();
    private static final NodeParser SIMPLIFIED_TEXT = NodeParser.builder().simplifiedTextFormat().build();

    public MessageToVanilla() {
        super(CommandProperties.create("message-to-vanilla", 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
            literal("quicktext").then(
                argument("message", greedyString())
                    .executes(ctx -> tellMessage(
                        ctx.getSource(),
                        getString(ctx, "message"),
                        QUICKTEXT
                    ))
            )
        ).then(
            literal("simplifiedtext").then(
                argument("message", greedyString())
                    .executes(ctx -> tellMessage(
                        ctx.getSource(),
                        getString(ctx, "message"),
                        SIMPLIFIED_TEXT
                    ))
            )
        );
    }

    protected int tellMessage(CommandSourceStack src, String message, NodeParser parser) {
        MutableComponent component = (MutableComponent) parser.parseText(message, PlaceholderContext.of(src).asParserContext());
        String vanillaJson = ComponentSerialization.CODEC.encodeStart(src.getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE), component).result()
            .map(JsonElement::toString).orElse("Failed to encode message!");
        src.sendSystemMessage(LocalizedMessage.builder("fabric-essentials.commands.message-to-vanilla")
            .addPlaceholder("preview", component)
            .addPlaceholder("vanilla_json", vanillaJson)
            .build());
        return 1;
    }

}
