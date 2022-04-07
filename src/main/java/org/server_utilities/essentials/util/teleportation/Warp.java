package org.server_utilities.essentials.util.teleportation;

import net.minecraft.nbt.CompoundTag;

public class Warp {

    private String name;
    private Location location;
    private boolean hasAlias;

    public Warp(String name, Location location, boolean hasAlias) {
        this.name = name;
        this.location = location;
        this.hasAlias = hasAlias;
    }

    public Warp(CompoundTag compoundTag) {
        this.name = compoundTag.getString("Name");
        this.location = new Location(compoundTag.getCompound("Location"));
        this.hasAlias = compoundTag.getBoolean("HasAlias");
    }

    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putString("Name", name);
        compoundTag.put("Location", location.save(new CompoundTag()));
        compoundTag.putBoolean("HasAlias", hasAlias);
        return compoundTag;
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
