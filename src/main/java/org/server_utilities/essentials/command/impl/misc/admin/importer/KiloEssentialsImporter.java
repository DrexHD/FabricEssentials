package org.server_utilities.essentials.command.impl.misc.admin.importer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec3;
import org.server_utilities.essentials.EssentialsMod;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;
import org.server_utilities.essentials.storage.ServerData;
import org.server_utilities.essentials.util.teleportation.Home;
import org.server_utilities.essentials.util.teleportation.Location;
import org.server_utilities.essentials.util.teleportation.Warp;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class KiloEssentialsImporter implements DataImporter {

    public static final KiloEssentialsImporter KILO_ESSENTIALS = new KiloEssentialsImporter();

    private KiloEssentialsImporter() {
    }

    @Override
    public String getImporterId() {
        return "kiloessentials";
    }

    @Override
    public void importData(MinecraftServer server) {
        File workingDir = new File(System.getProperty("user.dir"));
        Path data = workingDir.toPath().resolve("essentials").resolve("data");
        File users = data.resolve("users").toFile();
        if (users.exists()) {
            int failed = 0;
            int success = 0;
            for (File file : Objects.requireNonNull(users.listFiles())) {
                UUID uuid = UUID.fromString(file.getName().replace(".dat", ""));
                boolean shouldSave = false;
                try {
                    PlayerData playerData = DataStorage.getOfflinePlayerData(server, uuid);
                    CompoundTag tag = NbtIo.readCompressed(new FileInputStream(file), NbtAccounter.unlimitedHeap());
                    CompoundTag homesTag = tag.getCompound("homes");
                    Map<String, Home> homes = new HashMap<>();
                    for (String key : homesTag.getAllKeys()) {
                        CompoundTag loc = homesTag.getCompound(key).getCompound("loc");
                        CompoundTag pos = loc.getCompound("pos");
                        CompoundTag view = loc.getCompound("view");
                        homes.put(key, new Home(new Location(new Vec3(pos.getDouble("x"), pos.getDouble("y"), pos.getDouble("z")), view.getFloat("yaw"), view.getFloat("pitch"), new ResourceLocation(loc.getString("dim")))));
                    }
                    if (!homes.isEmpty()) {
                        shouldSave = true;
                        playerData.homes.putAll(homes);
                    }
                    CompoundTag settingsTag = tag.getCompound("settings");
                    if (settingsTag.contains("rtps_left")) {
                        shouldSave = true;
                        playerData.rtpCount = settingsTag.getInt("rtps_left");
                    }
                    if (shouldSave) DataStorage.updateOfflinePlayerData(server, uuid, playerData);
                    success++;
                } catch (Throwable e) {
                    EssentialsMod.LOGGER.error("An error occurred while parsing user file", e);
                    failed++;
                }
            }
            EssentialsMod.LOGGER.info("User data imported, {} successful, {} failed!", success, failed);
        } else {
            EssentialsMod.LOGGER.error("User directory ({}) doesn't exist", users);
        }
        File warpsFile = data.resolve("warps.dat").toFile();
        if (warpsFile.exists()) {
            try {
                CompoundTag tag = NbtIo.readCompressed(new FileInputStream(warpsFile), NbtAccounter.unlimitedHeap());
                Map<String, Warp> warps = new HashMap<>();
                for (String key : tag.getAllKeys()) {
                    CompoundTag loc = tag.getCompound(key).getCompound("loc");
                    CompoundTag pos = loc.getCompound("pos");
                    CompoundTag view = loc.getCompound("view");
                    warps.put(key, new Warp(new Location(new Vec3(pos.getDouble("x"), pos.getDouble("y"), pos.getDouble("z")), view.getFloat("yaw"), view.getFloat("pitch"), new ResourceLocation(loc.getString("dim")))));
                }
                ServerData serverData = DataStorage.serverData();
                serverData.getWarps().putAll(warps);
                EssentialsMod.LOGGER.info("Warps data imported, imported {} warps!", warps.size());
            } catch (Throwable e) {
                EssentialsMod.LOGGER.error("An error occurred while parsing warps file", e);
            }
        }


    }
}
