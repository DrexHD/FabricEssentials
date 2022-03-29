package org.server_utilities.essentials.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.server_utilities.essentials.teleportation.Home;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDataStorage {

    private final List<Home> homes = new ArrayList<>();

    // TODO: Exception handling
    public UserDataStorage(CompoundTag compoundTag) {
        load(compoundTag);
    }

    public void load(CompoundTag compoundTag) {
        // Homes
        if (compoundTag.contains("Homes")) {
            ListTag homesTag = compoundTag.getList("Homes", Tag.TAG_COMPOUND);
            for (int i = 0; i < homesTag.size(); i++) {
                homes.add(new Home(homesTag.getCompound(i)));
            }
        }
    }

    public CompoundTag save(CompoundTag compoundTag) {
        // Homes
        ListTag homesTag = new ListTag();
        for (Home home : homes) {
            homesTag.add(home.save(new CompoundTag()));
        }
        compoundTag.put("Homes", homesTag);

        return compoundTag;
    }

    public List<Home> getHomes() {
        return homes;
    }

    public Optional<Home> getHome(String name) {
        return homes.stream().filter(home -> home.getName().equals(name)).findFirst();
    }

}
