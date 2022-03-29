package org.server_utilities.essentials.teleportation;

import net.minecraft.nbt.CompoundTag;
import org.server_utilities.essentials.storage.util.Location;

public class Home {

    private String name;
    private Location location;

    public Home(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public Home(CompoundTag compoundTag) {
        this.name = compoundTag.getString("Name");
        this.location = new Location(compoundTag.getCompound("Location"));
    }

    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putString("Name", name);
        compoundTag.put("Location", location.save(new CompoundTag()));
        return compoundTag;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }
}
