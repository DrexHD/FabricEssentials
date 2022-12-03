package org.server_utilities.essentials.storage;

import org.server_utilities.essentials.config.ConfigManager;
import org.server_utilities.essentials.util.teleportation.Home;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {

    private final Map<String, Home> homes = new HashMap<>();
    public int rtpCount = ConfigManager.INSTANCE.config().rtp.defaultRtps;
    public boolean commandSpy = false;

    public Map<String, Home> getHomes() {
        return homes;
    }

}
