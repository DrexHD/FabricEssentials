package org.server_utilities.essentials.util;

import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.parsers.TextParserV1;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class StyledInputUtil {

    public static Component parse(String input, Predicate<TextParserV1.TextTag> predicate) {
        Map<String, TextParserV1.TagNodeBuilder> handlers = new HashMap<>();
        for (TextParserV1.TextTag textTag : TextParserV1.SAFE.getTags()) {
            if (predicate.test(textTag)) {
                handlers.put(textTag.name(), textTag.parser());
                if (textTag.aliases() != null) {
                    for (var alias : textTag.aliases()) {
                        handlers.put(alias, textTag.parser());
                    }
                }
            }
        }


        for (ChatFormatting formatting : ChatFormatting.values()) {
            if (handlers.get(formatting.getName()) != null) {
                input = input.replace(String.copyValueOf(new char[]{'&', formatting.getChar()}), "<" + formatting.getName() + ">");
            }
        }

        return TextParserUtils.formatText(input, handlers::get);
    }

}
