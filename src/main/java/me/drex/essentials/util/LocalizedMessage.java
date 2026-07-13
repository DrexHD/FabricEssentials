package me.drex.essentials.util;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagLikeParser;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.server.translations.api.Localization;
import xyz.nucleoid.server.translations.api.LocalizationTarget;

import java.util.Map;
import java.util.function.Function;

public interface LocalizedMessage {

    ParserContext.Key<Function<String, Component>> STATIC_PLACEHOLDERS = ParserContext.Key.of("fabric-essentials:static_placeholders");
    NodeParser PARSER = NodeParser.builder()
        .quickText()
        .simplifiedTextFormat()
        .serverPlaceholders()
        .placeholders(TagLikeParser.PLACEHOLDER_USER, STATIC_PLACEHOLDERS)
        .build();

    static MutableComponent localized(String key, CommandSourceStack src) {
        return localized(key, Map.of(), src, ServerPlaceholderContext.of(src));
    }

    static MutableComponent localized(String key, CommandSourceStack src, PlaceholderContext context) {
        return localized(key, Map.of(), src, context);
    }

    static MutableComponent localized(String key, Map<String, Component> placeholders, CommandSourceStack src) {
        return localized(key, placeholders, src, ServerPlaceholderContext.of(src));
    }

    static MutableComponent localized(String key, Map<String, Component> placeholders, CommandSourceStack src, @Nullable PlaceholderContext context) {
        ServerPlayer player = src.getPlayer();
        LocalizationTarget localizationTarget = player != null ? LocalizationTarget.of(player) : LocalizationTarget.ofSystem();
        String message = Localization.raw(key, localizationTarget);
        if (message == null) {
            message = key;
        }
        ParserContext parserContext;
        if (context != null) {
            parserContext = context.asParserContext();
        } else {
            parserContext = ParserContext.of();
        }
        parserContext.with(STATIC_PLACEHOLDERS, placeholderGetter(placeholders));
        return (MutableComponent) PARSER.parseComponent(TextNode.of(message), parserContext);
    }

    private static Function<String, Component> placeholderGetter(Map<String, Component> placeholders) {
        return key -> placeholders.getOrDefault(key, Component.literal("${" + key + "}"));
    }

}
