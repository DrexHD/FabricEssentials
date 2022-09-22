package org.server_utilities.essentials.storage;

import org.server_utilities.essentials.util.teleportation.Warp;

import java.util.*;

public class ServerData {

    private final Map<String, Warp> warps = new HashMap<>();

    protected ServerData() {
    }

    public Map<String, Warp> getWarps() {
        return this.warps;
    }

}
