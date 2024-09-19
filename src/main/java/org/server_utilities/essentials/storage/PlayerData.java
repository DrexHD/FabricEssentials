package org.server_utilities.essentials.storage;

import org.server_utilities.essentials.util.teleportation.Home;
import org.server_utilities.essentials.util.teleportation.Location;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PlayerData {

    public final Map<String, Home> homes = new HashMap<>();
    public Deque<Location> teleportLocations = new LinkedList<>();
    public boolean commandSpy = false;

}
