package me.drex.essentials.command.impl.misc.admin.importer;

import me.drex.essentials.EssentialsMod;
import me.drex.essentials.storage.DataStorage;
import me.drex.essentials.storage.ServerData;
import me.drex.essentials.util.teleportation.Home;
import me.drex.essentials.util.teleportation.Location;
import me.drex.essentials.util.teleportation.Warp;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class EssentialCommandsImporter implements DataImporter {
    public static final EssentialCommandsImporter ESSENTIAL_COMMANDS = new EssentialCommandsImporter();

    @Override
    public String getImporterId() {
        return "essential_commands";
    }

    @Override
    public void importData(MinecraftServer server) {
        var worldPath = server.getWorldPath(LevelResource.ROOT);
        var modPlayerData = worldPath.resolve("modplayerdata");
        // warps player data
        if (Files.exists(modPlayerData)) {
            try {
                AtomicInteger success = new AtomicInteger(0);
                AtomicInteger failures = new AtomicInteger(0);
                Files.list(modPlayerData).forEach(path -> {
                    if (Files.isRegularFile(path)) {
                        try {
                            boolean shouldSave = false;
                            var uuid = UUID.fromString(path.getFileName().toString().replace(".dat", ""));
                            var playerData = DataStorage.getOfflinePlayerData(server, uuid);
                            var tag = NbtIo.readCompressed(Files.newInputStream(path), NbtAccounter.unlimitedHeap());
                            Map<String, Home> homes = new HashMap<>();

                            //? if >= 1.21.5 {
                            var data = tag.getCompound("data").orElseThrow();
                            var homesData = data.getCompound("homes").orElseThrow();
                            for (String homeName : homesData.keySet()) {
                                var home = homesData.getCompound(homeName).orElseThrow();
                                var world = home.getString("WorldRegistryKey").orElseThrow();
                                var x = home.getDouble("x").orElseThrow();
                                var y = home.getDouble("y").orElseThrow();
                                var z = home.getDouble("z").orElseThrow();
                                var yaw = home.getFloat("headYaw").orElseThrow();
                                var pitch = home.getFloat("pitch").orElseThrow();
                                homes.put(homeName, new Home(new Location(new Vec3(x, y, z), yaw, pitch, ResourceLocation.parse(world))));
                            }
                            //?} else {
                            /*var data = tag.getCompound("data");
                            var homesData = data.getCompound("homes");
                            for (String homeName : homesData.getAllKeys()) {
                                var home = homesData.getCompound(homeName);
                                var world = home.getString("WorldRegistryKey");
                                var x = home.getDouble("x");
                                var y = home.getDouble("y");
                                var z = home.getDouble("z");
                                var yaw = home.getFloat("headYaw");
                                var pitch = home.getFloat("pitch");
                                homes.put(homeName, new Home(new Location(new Vec3(x, y, z), yaw, pitch, ResourceLocation.parse(world))));
                            }
                            *///?}
                            if (!homes.isEmpty()) {
                                shouldSave = true;
                                playerData.homes.putAll(homes);
                            }
                            if (shouldSave) DataStorage.updateOfflinePlayerData(server, uuid, playerData);
                            success.incrementAndGet();
                        } catch (Exception e) {
                            EssentialsMod.LOGGER.error("An error occurred while handling user file {}", path, e);
                            failures.incrementAndGet();
                        }
                    }
                });
                EssentialsMod.LOGGER.info("User data imported, {} successful, {} failed!", success.get(), failures.get());

            } catch (IOException e) {
                EssentialsMod.LOGGER.error("Failed to import player data", e);
            }
        } else {
            EssentialsMod.LOGGER.error("User directory ({}) doesn't exist", modPlayerData);
        }
        var essentialCommands = worldPath.resolve("essentialcommands");
        if (Files.exists(essentialCommands)) {
            Path worldData = essentialCommands.resolve("world_data.dat");
            if (Files.exists(worldData)) {
                try {
                    var tag = NbtIo.readCompressed(Files.newInputStream(worldData), NbtAccounter.unlimitedHeap());
                    Map<String, Warp> warps = new HashMap<>();

                    //? if >= 1.21.5 {
                    var data = tag.getCompound("data").orElseThrow();

                    var warpsData = data.getCompound("warps").orElseThrow();
                    for (String homeName : warpsData.keySet()) {
                        var home = warpsData.getCompound(homeName).orElseThrow();
                        var world = home.getString("WorldRegistryKey").orElseThrow();
                        var x = home.getDouble("x").orElseThrow();
                        var y = home.getDouble("y").orElseThrow();
                        var z = home.getDouble("z").orElseThrow();
                        var yaw = home.getFloat("headYaw").orElseThrow();
                        var pitch = home.getFloat("pitch").orElseThrow();
                        warps.put(homeName, new Warp(new Location(new Vec3(x, y, z), yaw, pitch, ResourceLocation.parse(world))));
                    }
                    //?} else {
                    /*var data = tag.getCompound("data");
                    var warpsData = data.getCompound("warps");
                    for (String homeName : warpsData.getAllKeys()) {
                        var home = warpsData.getCompound(homeName);
                        var world = home.getString("WorldRegistryKey");
                        var x = home.getDouble("x");
                        var y = home.getDouble("y");
                        var z = home.getDouble("z");
                        var yaw = home.getFloat("headYaw");
                        var pitch = home.getFloat("pitch");
                        warps.put(homeName, new Warp(new Location(new Vec3(x, y, z), yaw, pitch, ResourceLocation.parse(world))));
                    }
                    *///?}
                    ServerData serverData = DataStorage.serverData();
                    serverData.getWarps().putAll(warps);
                    EssentialsMod.LOGGER.info("Warps data imported, imported {} warps!", warps.size());
                } catch (IOException e) {
                    EssentialsMod.LOGGER.error("An error occurred while handling the server file {}", worldData, e);
                }
            } else {
                EssentialsMod.LOGGER.error("Server data ({}) doesn't exist", worldData);
            }

        } else {
            EssentialsMod.LOGGER.error("Server data directory ({}) doesn't exist", essentialCommands);
        }
    }

}
