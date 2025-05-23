package me.drex.essentials.command.impl.misc.admin.importer;

import me.drex.essentials.EssentialsMod;
import me.drex.essentials.storage.DataStorage;
import me.drex.essentials.storage.PlayerData;
import me.drex.essentials.storage.ServerData;
import me.drex.essentials.util.teleportation.Home;
import me.drex.essentials.util.teleportation.Location;
import me.drex.essentials.util.teleportation.Warp;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec3;

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
                    Map<String, Home> homes = new HashMap<>();
                    //? if >= 1.21.5 {
                    CompoundTag homesTag = tag.getCompoundOrEmpty("homes");
                    for (String key : homesTag.keySet()) {
                        CompoundTag loc = homesTag.getCompound(key).orElseThrow().getCompound("loc").orElseThrow();
                        CompoundTag pos = loc.getCompound("pos").orElseThrow();
                        CompoundTag view = loc.getCompound("view").orElseThrow();
                        homes.put(key,
                            new Home(
                                new Location(
                                    new Vec3(
                                        pos.getDouble("x").orElseThrow(),
                                        pos.getDouble("y").orElseThrow(),
                                        pos.getDouble("z").orElseThrow()
                                    ),
                                    view.getFloat("yaw").orElseThrow(),
                                    view.getFloat("pitch").orElseThrow(),
                                    ResourceLocation.parse(loc.getString("dim").orElseThrow())
                                )
                            )
                        );
                    }
                    //?} else {
                    /*CompoundTag homesTag = tag.getCompound("homes");
                    for (String key : homesTag.getAllKeys()) {
                        CompoundTag loc = homesTag.getCompound(key).getCompound("loc");
                        CompoundTag pos = loc.getCompound("pos");
                        CompoundTag view = loc.getCompound("view");
                        homes.put(key,
                            new Home(
                                new Location(
                                    new Vec3(pos.getDouble("x"), pos.getDouble("y"), pos.getDouble("z")),
                                    view.getFloat("yaw"),
                                    view.getFloat("pitch"),
                                    ResourceLocation.parse(loc.getString("dim")))
                            )
                        );
                    }
                    *///?}
                    if (!homes.isEmpty()) {
                        shouldSave = true;
                        playerData.homes.putAll(homes);
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
                //? if >= 1.21.5 {
                for (String key : tag.keySet()) {
                    CompoundTag loc = tag.getCompound(key).orElseThrow().getCompound("loc").orElseThrow();
                    CompoundTag pos = loc.getCompound("pos").orElseThrow();
                    CompoundTag view = loc.getCompound("view").orElseThrow();
                    warps.put(key,
                        new Warp(
                            new Location(
                                new Vec3(
                                    pos.getDouble("x").orElseThrow(),
                                    pos.getDouble("y").orElseThrow(),
                                    pos.getDouble("z").orElseThrow()
                                ),
                                view.getFloat("yaw").orElseThrow(),
                                view.getFloat("pitch").orElseThrow(),
                                ResourceLocation.parse(loc.getString("dim").orElseThrow())
                            )
                        )
                    );
                }
                //?} else {
                /*for (String key : tag.getAllKeys()) {
                    CompoundTag loc = tag.getCompound(key).getCompound("loc");
                    CompoundTag pos = loc.getCompound("pos");
                    CompoundTag view = loc.getCompound("view");
                    warps.put(key,
                        new Warp(
                            new Location(
                                new Vec3(pos.getDouble("x"), pos.getDouble("y"), pos.getDouble("z")),
                                view.getFloat("yaw"),
                                view.getFloat("pitch"),
                                ResourceLocation.parse(loc.getString("dim")))
                        )
                    );
                }
                *///?}
                ServerData serverData = DataStorage.serverData();
                serverData.getWarps().putAll(warps);
                EssentialsMod.LOGGER.info("Warps data imported, imported {} warps!", warps.size());
            } catch (Throwable e) {
                EssentialsMod.LOGGER.error("An error occurred while parsing warps file", e);
            }
        }


    }
}
