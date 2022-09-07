package org.server_utilities.essentials.storage;

import org.server_utilities.essentials.config.ConfigManager;
import org.server_utilities.essentials.util.teleportation.Home;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class PlayerData {

    public final List<Home> homes = new LinkedList<>();
    public int rtpsLeft = ConfigManager.INSTANCE.config().rtpConfig.defaultRtps;

    public List<Home> getHomes() {
        return homes;
    }

    public Optional<Home> getHome(String name) {
        return homes.stream().filter(home -> home.name().equals(name)).findFirst();
    }

}
