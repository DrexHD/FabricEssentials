package org.server_utilities.essentials.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class ComponentUtil {

    public static MutableComponent join(Component... elements) {
        return join(new TextComponent(", ").withStyle(ChatFormatting.WHITE), ChatFormatting.GRAY, ChatFormatting.DARK_GRAY, elements);
    }

    public static MutableComponent join(Component delimiter, ChatFormatting primary, ChatFormatting secondary, Component... elements) {
        MutableComponent component = new TextComponent("");
        for (int i = 0; i < elements.length; i++) {
            component.append(((MutableComponent) elements[i]).withStyle(i % 2 == 0 ? primary : secondary));
            if (i < elements.length - 1) component.append(delimiter);
        }
        return component;
    }

}
