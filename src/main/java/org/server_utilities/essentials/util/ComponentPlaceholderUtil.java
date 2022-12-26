package org.server_utilities.essentials.util;

import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ComponentPlaceholderUtil {

    public static Map<String, Component> exceptionPlaceholders(Throwable throwable) {
        return new HashMap<>() {{
            put("exception_message", Component.literal(throwable.getMessage()));
            put("exception_description", Component.literal(throwable.toString()));
            put("exception_stacktrace", Component.literal(
                    String.join("\n", Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).toList())
            ));
        }};
    }

    public static Map<String, Component> modPlaceholders(ModContainer mod) {
        return new HashMap<>() {{
            put("mod_id", Component.literal(mod.getMetadata().getId()));
            put("mod_name", Component.literal(mod.getMetadata().getName()));
            put("mod_description", Component.literal(mod.getMetadata().getDescription()));
            put("mod_type", Component.literal(mod.getMetadata().getType()));
            put("mod_authors", ComponentUtils.formatList(mod.getMetadata().getAuthors(), person -> Component.literal(person.getName())));
            put("mod_version", Component.literal(mod.getMetadata().getVersion().getFriendlyString()));
        }};
    }

    @SafeVarargs
    public static Map<String, Component> mergePlaceholderMaps(Map<String, Component>... maps) {
        Map<String, Component> result = new HashMap<>();
        for (Map<String, Component> map : maps) {
            result.putAll(map);
        }
        return result;
    }

}
