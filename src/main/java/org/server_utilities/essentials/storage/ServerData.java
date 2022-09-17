package org.server_utilities.essentials.storage;

import org.server_utilities.essentials.util.teleportation.Warp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServerData {

    private final List<Warp> warps = new ArrayList<>();

    public ServerData() {
    }

    public List<Warp> getWarps() {
        return warps;
    }

    public Optional<Warp> getWarp(String name) {
        return warps.stream().filter(warp -> warp.name().equals(name)).findFirst();
    }

}
