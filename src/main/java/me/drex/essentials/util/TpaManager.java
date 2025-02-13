package me.drex.essentials.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.config.ConfigManager;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class TpaManager {

    public static final TpaManager INSTANCE = new TpaManager();

    private final HashMap<Participants, Request> requestMap = new HashMap<>();

    private TpaManager() {
        ServerTickEvents.START_SERVER_TICK.register(this::onTick);
    }

    private void onTick(MinecraftServer server) {
        requestMap.entrySet().removeIf(entry -> (entry.getValue().timeStamp + TimeUnit.SECONDS.toMillis(ConfigManager.config().tpa.expiry)) <= System.currentTimeMillis());
    }

    public void addRequest(Participants participants, Direction direction) {
        requestMap.put(participants, new Request(direction, System.currentTimeMillis()));
    }

    public Request removeRequest(Participants participants) {
        return requestMap.remove(participants);
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
        HERE("tpahere", CommandProperties.create("tpahere", new String[]{"tprhere"}, 0)),
        THERE("tpa", CommandProperties.create("tpa", new String[]{"tpr"}, 0));

        private final String translationKey;
        private final CommandProperties commandProperties;

        Direction(String translationKey, CommandProperties commandProperties) {
            this.translationKey = translationKey;
            this.commandProperties = commandProperties;
        }

        public String getTranslationKey() {
            return translationKey;
        }

        public CommandProperties getProperties() {
            return commandProperties;
        }
    }

    public record Request(Direction direction, long timeStamp) {
    }

    public List<Participants> getRequestsFor(UUID target) {
        return requestMap.keySet().stream()
            .filter(request -> request.requested().equals(target))
            .toList();
    }

}
