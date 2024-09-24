package me.drex.essentials.storage;

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

}
