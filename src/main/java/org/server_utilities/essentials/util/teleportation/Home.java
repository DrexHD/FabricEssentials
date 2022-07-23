package org.server_utilities.essentials.util.teleportation;

public final class Home {

    private final String name;
    private final Location location;

    public Home(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String name() {
        return name;
    }

    public Location location() {
        return location;
    }

}