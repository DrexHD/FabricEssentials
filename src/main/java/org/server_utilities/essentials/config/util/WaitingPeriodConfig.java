package org.server_utilities.essentials.config.util;

import me.drex.message.api.Message;
import net.minecraft.network.chat.Component;
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
        DAMAGE("fabric-essentials.teleport.cancel.damage"),
        MOVE("fabric-essentials.teleport.cancel.move"),
        UNKNOWN("fabric-essentials.teleport.cancel.unknown");

        private final String translationKeySelf;

        WaitingResult(String translationKeySelf) {
            this.translationKeySelf = translationKeySelf;
        }

        public Component component() {
            return Message.message(translationKeySelf);
        }

    }

}
