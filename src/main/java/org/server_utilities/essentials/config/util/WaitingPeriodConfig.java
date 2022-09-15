package org.server_utilities.essentials.config.util;

import org.server_utilities.essentials.util.KeyUtil;
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

    private static final String CANCEL_TRANSLATION_KEY = KeyUtil.translation("teleport", "cancel");

    public enum WaitingResult {
        DAMAGE(KeyUtil.join(CANCEL_TRANSLATION_KEY, "damage")),
        MOVE(KeyUtil.join(CANCEL_TRANSLATION_KEY, "move")),
        OFFLINE(KeyUtil.join(CANCEL_TRANSLATION_KEY, "offline")),
        SUCCESS();

        private final String translationKeySelf;
        private final boolean cancelled;

        WaitingResult() {
            this.translationKeySelf = null;
            this.cancelled = false;
        }

        WaitingResult(String translationKeySelf) {
            this.translationKeySelf = translationKeySelf;
            this.cancelled = true;
        }

        public String getTranslationKeySelf() {
            return translationKeySelf;
        }

        public String getTranslationKeyOther() {
            return KeyUtil.join(translationKeySelf, "other");
        }

        public boolean isCancelled() {
            return cancelled;
        }
    }

}
