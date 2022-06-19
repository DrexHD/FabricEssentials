package org.server_utilities.essentials.storage;

import com.mojang.datafixers.DataFixer;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.NotNull;
import org.server_utilities.essentials.EssentialsMod;
import org.slf4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * A storage similar to {@link net.minecraft.world.level.storage.PlayerDataStorage} for storing data that should persist, even when the server is loaded without the mod.
 */
public class EssentialsDataStorage {

    private static final Logger LOGGER = EssentialsMod.getLogger();
    private final File essentialsDir;
    private final File essentialsPlayerDir;
    protected final DataFixer fixerUpper;
    private final Map<UUID, UserData> essentialsPlayerData = new HashMap<>();
    private EssentialsData essentialsData = new EssentialsData(new CompoundTag());

    public EssentialsDataStorage(LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer dataFixer) {
        this.fixerUpper = dataFixer;
        this.essentialsDir = levelStorageAccess.getLevelPath(EssentialsMod.ESSENTIALS_DATA_DIR).toFile();
        this.essentialsPlayerDir = levelStorageAccess.getLevelPath(EssentialsMod.ESSENTIALS_DATA_DIR).resolve("players").toFile();
        this.essentialsPlayerDir.mkdirs();
        load();
    }

    public void save(PlayerList playerList, UUID uuid) {
        try {
            UserData userData = getUserData(uuid);
            CompoundTag compoundTag = userData.save(new CompoundTag());
            File tempFile = File.createTempFile(uuid + "-", ".dat", this.essentialsPlayerDir);
            NbtIo.writeCompressed(compoundTag, tempFile);
            File file = new File(this.essentialsPlayerDir, uuid + ".dat");
            File oldFile = new File(this.essentialsPlayerDir, uuid + ".dat_old");
            Util.safeReplaceFile(file, tempFile, oldFile);
            // Remove cached data, if the player is not online
            if (playerList.getPlayer(uuid) == null) essentialsPlayerData.remove(uuid);
        } catch (Exception exception) {
            LOGGER.warn("Failed to save essentials player data for {}!", uuid);
        }
    }

    public UserData load(UUID uuid) {
        // If the data is already loaded (eg. by commands), use that instead of loading the old version again
        if (essentialsPlayerData.containsKey(uuid)) return essentialsPlayerData.get(uuid);
        CompoundTag compoundTag;
        try {
            File file = new File(this.essentialsPlayerDir, uuid + ".dat");
            if (file.exists() && file.isFile()) {
                compoundTag = NbtIo.readCompressed(file);
                UserData userData = new UserData(compoundTag);
                essentialsPlayerData.put(uuid, userData);
                return userData;
            }
        } catch (Exception exception) {
            LOGGER.warn("Failed to load essentials player data for {}!", uuid);
        }
        return new UserData(new CompoundTag());
    }

    public void save() {
        try {
            CompoundTag compoundTag = essentialsData.save(new CompoundTag());
            File tempFile = File.createTempFile("data-", ".dat", this.essentialsDir);
            NbtIo.writeCompressed(compoundTag, tempFile);
            File file = new File(this.essentialsDir, "data.dat");
            File oldFile = new File(this.essentialsDir, "data.dat_old");
            Util.safeReplaceFile(file, tempFile, oldFile);
        } catch (Exception exception) {
            LOGGER.warn("Failed to save essentials data!");
        }
    }

    public void load() {
        try {
            CompoundTag compoundTag;
            File file = new File(this.essentialsDir, "data.dat");
            if (file.exists() && file.isFile()) {
                compoundTag = NbtIo.readCompressed(file);
                essentialsData = new EssentialsData(compoundTag);
            }
        } catch (Exception exception) {
            LOGGER.warn("Failed to load essentials data!");
        }
    }

    public EssentialsData getEssentialsData() {
        return essentialsData;
    }

    public Map<UUID, UserData> getEssentialsPlayerData() {
        return essentialsPlayerData;
    }

    @NotNull
    public UserData getUserData(UUID uuid) {
        UserData dataStorage = essentialsPlayerData.get(uuid);
        if (dataStorage == null) {
            dataStorage = load(uuid);
        }
        return dataStorage;
    }

}
