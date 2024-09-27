package me.drex.essentials.storage;

import me.drex.essentials.config.ConfigManager;
import me.drex.essentials.util.teleportation.Home;
import me.drex.essentials.util.teleportation.Location;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PlayerData {

    public final Map<String, Home> homes = new HashMap<>();
    public Deque<Location> teleportLocations = new LinkedList<>();
    public boolean commandSpy = false;

    public void saveLocation(Location location) {
        teleportLocations.push(location);
        // Enforce limit
        int toRemove = teleportLocations.size() - ConfigManager.config().teleportation.savedBackLocations;
        for (int i = 0; i < toRemove; i++) {
            teleportLocations.removeLast();
        }
    }

}
