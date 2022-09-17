package org.server_utilities.essentials.command.impl.util.importer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec3;
import org.server_utilities.essentials.EssentialsMod;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;
import org.server_utilities.essentials.util.teleportation.Home;
import org.server_utilities.essentials.util.teleportation.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        File users = workingDir.toPath().resolve("essentials").resolve("data").resolve("users").toFile();
        int failed = 0;
        int success = 0;
        if (users.exists()) {
            for (File file : Objects.requireNonNull(users.listFiles())) {
                UUID uuid = UUID.fromString(file.getName().replace(".dat", ""));
                try {
                    CompoundTag tag = NbtIo.readCompressed(new FileInputStream(file));
                    CompoundTag homesTag = tag.getCompound("homes");
                    List<Home> homes = new ArrayList<>();
                    for (String key : homesTag.getAllKeys()) {
                        CompoundTag loc = homesTag.getCompound(key).getCompound("loc");
                        CompoundTag pos = loc.getCompound("pos");
                        CompoundTag view = loc.getCompound("view");
                        homes.add(new Home(key, new Location(new Vec3(pos.getDouble("x"), pos.getDouble("y"), pos.getDouble("z")), view.getFloat("yaw"), view.getFloat("pitch"), new ResourceLocation(loc.getString("dim")))));
                    }
                    if (!homes.isEmpty()) {
                        PlayerData playerData = DataStorage.STORAGE.getPlayerData(server, uuid);
                        playerData.getHomes().addAll(homes);
                    }
                    success++;
                } catch (IOException e) {
                    EssentialsMod.LOGGER.error("An error occurred while reading user file", e);
                    failed++;
                }
            }
            EssentialsMod.LOGGER.info("Data imported, {} successful, {} failed!", success, failed);
        } else {
            EssentialsMod.LOGGER.error("User directory ({}) doesn't exist", users);
        }
    }
}
