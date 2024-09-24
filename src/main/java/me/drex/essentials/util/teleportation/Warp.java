package me.drex.essentials.util.teleportation;

import net.minecraft.network.chat.Component;
import me.drex.essentials.util.ComponentPlaceholderUtil;

import java.util.HashMap;
import java.util.Map;

public record Warp(Location location) {

    public Map<String, Component> placeholders(String name) {
        return ComponentPlaceholderUtil.mergePlaceholderMaps(new HashMap<>(){{
            put("warp_name", Component.literal(name));
        }}, location().placeholders());
    }

}
