package org.server_utilities.essentials.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class ComponentUtil {

    public static MutableComponent join(Component delimiter, Component... elements) {
        MutableComponent component = new TextComponent("");
        for (int i = 0; i < elements.length; i++) {
            component.append(elements[i]);
            if (i < elements.length - 1) component.append(delimiter);
        }
        return component;
    }

}
