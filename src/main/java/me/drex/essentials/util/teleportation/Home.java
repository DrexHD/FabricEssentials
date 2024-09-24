package me.drex.essentials.util.teleportation;

import net.minecraft.network.chat.Component;
import me.drex.essentials.util.ComponentPlaceholderUtil;

import java.util.HashMap;
import java.util.Map;

public record Home(Location location) {

    public Map<String, Component> placeholders(String name) {
        return ComponentPlaceholderUtil.mergePlaceholderMaps(new HashMap<>(){{
            put("home_name", Component.literal(name));
        }}, location().placeholders());
    }

}