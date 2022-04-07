package org.server_utilities.essentials.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.server_utilities.essentials.util.teleportation.Warp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EssentialsData {

    private final List<Warp> warps = new ArrayList<>();

    public EssentialsData(CompoundTag compoundTag) {
        load(compoundTag);
    }

    public void load(CompoundTag compoundTag) {
        // Warps
        if (compoundTag.contains("Warps")) {
            ListTag warpsTag = compoundTag.getList("Warps", Tag.TAG_COMPOUND);
            for (int i = 0; i < warpsTag.size(); i++) {
                warps.add(new Warp(warpsTag.getCompound(i)));
            }
        }
    }

    public CompoundTag save(CompoundTag compoundTag) {
        // Warps
        ListTag warpsTag = new ListTag();
        for (Warp warp : warps) {
            warpsTag.add(warp.save(new CompoundTag()));
        }
        compoundTag.put("Warps", warpsTag);

        return compoundTag;
    }

    public List<Warp> getWarps() {
        return warps;
    }

    public Optional<Warp> getWarp(String name) {
        return warps.stream().filter(home -> home.getName().equals(name)).findFirst();
    }

}
