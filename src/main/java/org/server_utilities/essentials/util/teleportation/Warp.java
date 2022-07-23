package org.server_utilities.essentials.util.teleportation;

public class Warp {

    private String name;
    private Location location;
    private boolean hasAlias;

    public Warp(String name, Location location, boolean hasAlias) {
        this.name = name;
        this.location = location;
        this.hasAlias = hasAlias;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public boolean hasAlias() {
        return hasAlias;
    }

}
