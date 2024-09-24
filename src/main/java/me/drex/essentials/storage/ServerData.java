package me.drex.essentials.storage;

import me.drex.essentials.util.teleportation.Warp;

import java.util.HashMap;
import java.util.Map;

public class ServerData {

    private final Map<String, Warp> warps = new HashMap<>();

    protected ServerData() {
    }

    public Map<String, Warp> getWarps() {
        return this.warps;
    }

}
