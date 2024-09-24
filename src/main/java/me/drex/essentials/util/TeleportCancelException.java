package me.drex.essentials.util;

import net.minecraft.network.chat.Component;

public class TeleportCancelException extends Exception {

    private final Component message;

    public TeleportCancelException(final Component message) {
        super(message.getString());
        this.message = message;
    }

    public Component getRawMessage() {
        return message;
    }

}
