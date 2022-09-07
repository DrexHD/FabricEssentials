package org.server_utilities.essentials.config.util;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class WaitingPeriodConfig {

    @Comment("The amount of seconds the player has to wait before the command is executed")
    public int period = 0;

    @Comment("Config section for configuring cancellation reasons")
    public CancellationConfig cancellation = new CancellationConfig();

    @ConfigSerializable
    public static class CancellationConfig {

        @Comment("If enabled, teleports will get cancelled if the player is damaged during the waiting period")
        public boolean damage = false;

        @Comment("If set to 0 or greater the teleport will get cancelled if the player moves more than the specified amount of blocks")
        public int maxMoveDistance = -1;

    }

    public enum WaitingResult {
        DAMAGE("text.fabric-essentials.teleport.cancel.damage", "text.fabric-essentials.teleport.cancel.damage.other"),
        MOVE("text.fabric-essentials.teleport.cancel.move", "text.fabric-essentials.teleport.cancel.move.other"),
        OFFLINE("", "text.fabric-essentials.teleport.cancel.offline.other"),
        SUCCESS();

        private final String translationKeySelf;
        private final String translationKeyOther;

        private final boolean cancelled;
        WaitingResult() {
            this.translationKeySelf = null;
            this.translationKeyOther = null;
            this.cancelled = false;
        }

        WaitingResult(String translationKeySelf, String translationKeyOther) {
            this.translationKeySelf = translationKeySelf;
            this.translationKeyOther = translationKeyOther;
            this.cancelled = true;
        }

        public String getTranslationKeySelf() {
            return translationKeySelf;
        }

        public String getTranslationKeyOther() {
            return translationKeyOther;
        }

        public boolean isCancelled() {
            return cancelled;
        }
    }

}
