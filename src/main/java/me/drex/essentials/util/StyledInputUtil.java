package me.drex.essentials.util;

import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import eu.pb4.placeholders.api.parsers.tag.TextTag;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

public class StyledInputUtil {

    public static Component parse(String input, CommandSourceStack source, String basePermission) {
        return parse(input, source, basePermission, true);
    }

    public static Component parse(String input, CommandSourceStack source, String basePermission, boolean safe) {
        return parseNode(input, source, basePermission, safe).toText();
    }

    public static Component parse(String input, Predicate<TextTag> predicate, boolean safe) {
        return parseNode(input, predicate, safe).toText();
    }

    public static TextNode parseNode(String input, CommandSourceStack source, String basePermission, boolean safe) {
        return parseNode(input, textTag -> IdentifierUtil.check(source, basePermission + textTag.name()), safe);
    }

    public static TextNode parseNode(String input, Predicate<TextTag> predicate, boolean safe) {
        return TextNode.asSingle(
            createParser(predicate, safe).parseNodes(TextNode.of(input))
        );
    }

    public static NodeParser createParser(Predicate<TextTag> predicate, boolean safe) {
        var builder = NodeParser.builder();

        var tags = getTextTagRegistry(predicate, safe);
        if (!tags.getTags().isEmpty()) {
            builder.simplifiedTextFormat();
            builder.quickText();
            builder.customTagRegistry(tags);
        }
        builder.legacyVanillaColor();
        return builder.build();
    }

    public static TagRegistry getTextTagRegistry(Predicate<TextTag> predicate, boolean safe) {
        return getTextTagRegistry(predicate.and(textTag -> {
            if (safe) {
                return textTag.userSafe();
            }
            return true;
        }));
    }

    public static TagRegistry getTextTagRegistry(Predicate<TextTag> predicate) {
        var registry = TagRegistry.create();

        for (var entry : TagRegistry.DEFAULT.getTags()) {
            if (predicate.test(entry)) {
                registry.register(entry);
            }
        }
        return registry;
    }

}
