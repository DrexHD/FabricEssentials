package org.server_utilities.essentials.config.teleportation;

import net.minecraft.network.chat.Component;

import static me.drex.message.api.LocalizedMessage.localized;

public class WaitingPeriodConfig {

    public int period = 0;

    public CancellationConfig cancellation = new CancellationConfig();

    public static class CancellationConfig {

        public boolean damage = false;

        public int maxMoveDistance = -1;

    }

    public enum WaitingResult {
        DAMAGE("fabric-essentials.teleport.cancel.damage"),
        MOVE("fabric-essentials.teleport.cancel.move"),
        UNKNOWN("fabric-essentials.teleport.cancel.unknown");

        private final String translationKeySelf;

        WaitingResult(String translationKeySelf) {
            this.translationKeySelf = translationKeySelf;
        }

        public Component component() {
            return localized(translationKeySelf);
        }

    }

}
