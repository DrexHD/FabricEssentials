package org.server_utilities.essentials.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.server_utilities.essentials.command.Properties;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TprManager {

    public static final TprManager INSTANCE = new TprManager();

    private final HashMap<Participants, Request> requestMap = new HashMap<>();

    private TprManager() {
        ServerTickEvents.START_SERVER_TICK.register(this::onTick);
    }

    private void onTick(MinecraftServer server) {
        // TODO: config
        long cooldown = 30;
        // TODO: remove if player is offline
        requestMap.entrySet().removeIf(entry -> (entry.getValue().timeStamp + TimeUnit.SECONDS.toMillis(cooldown)) <= System.currentTimeMillis());
    }

    public void addRequest(Participants participants, Direction direction) {
        requestMap.put(participants, new Request(direction, System.currentTimeMillis()));
    }

    @Nullable
    public Direction getRequest(Participants participant) {
        Request request = requestMap.get(participant);
        if (request != null) {
            return request.direction();
        }
        return null;
    }

    public record Participants(UUID requester, UUID requested) {
    }

    public enum Direction {
        HERE("here", Properties.create("tpahere", "tprhere").permission("tpahere")),
        THERE("there", Properties.create("tpa", "tpr").permission("tpa"));

        private final String translationKey;
        private final Properties properties;

        Direction(String translationKey, Properties properties) {
            this.translationKey = translationKey;
            this.properties = properties;
        }

        public String getTranslationKey() {
            return translationKey;
        }

        public Properties getProperties() {
            return properties;
        }
    }

    public record Request(Direction direction, long timeStamp) {
    }

}
