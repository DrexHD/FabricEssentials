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
* */
public class EssentialsDataStorage {

    private static final Logger LOGGER = EssentialsMod.getLogger();
    private final File essentialsDir;
    protected final DataFixer fixerUpper;
    private final Map<UUID, UserDataStorage> essentialsPlayerData = new HashMap<>();

    public EssentialsDataStorage(LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer dataFixer) {
        this.fixerUpper = dataFixer;
        this.essentialsDir = levelStorageAccess.getLevelPath(EssentialsMod.ESSENTIALS_DATA_DIR).toFile();
        this.essentialsDir.mkdirs();
    }

    public void save(PlayerList playerList, UUID uuid) {
        try {
            UserDataStorage userDataStorage = getUserData(uuid);
            CompoundTag compoundTag = userDataStorage.save(new CompoundTag());
            File tempFile = File.createTempFile(uuid + "-", ".dat", this.essentialsDir);
            NbtIo.writeCompressed(compoundTag, tempFile);
            File file = new File(this.essentialsDir, uuid + ".dat");
            File oldFile = new File(this.essentialsDir, uuid + ".dat_old");
            Util.safeReplaceFile(file, tempFile, oldFile);
            // Remove cached data, if the player is not online
            if (playerList.getPlayer(uuid) == null) essentialsPlayerData.remove(uuid);
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to save essentials player data for {}", uuid);
        }
    }

    public UserDataStorage load(UUID uuid) {
        // If the data is already loaded (eg. by commands), use that instead of loading the old version again
        if (essentialsPlayerData.containsKey(uuid)) return essentialsPlayerData.get(uuid);
        CompoundTag compoundTag = null;
        try {
            File file = new File(this.essentialsDir, uuid + ".dat");
            if (file.exists() && file.isFile()) {
                compoundTag = NbtIo.readCompressed(file);
            }
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to load essentials player data for {}", uuid);
        }
        UserDataStorage userDataStorage;
        if (compoundTag != null) {
            userDataStorage = new UserDataStorage(compoundTag);
            essentialsPlayerData.put(uuid, userDataStorage);
            //NbtUtils.update(this.fixerUpper, DataFixTypes.ESSENTIALS_PLAYER, compoundTag, dataVersion)
        } else {
            userDataStorage = new UserDataStorage(new CompoundTag());
        }
        return userDataStorage;
    }

    public Map<UUID, UserDataStorage> getEssentialsPlayerData() {
        return essentialsPlayerData;
    }

    @NotNull
    public UserDataStorage getUserData(UUID uuid) {
        UserDataStorage dataStorage = essentialsPlayerData.get(uuid);
        if (dataStorage == null) {
            dataStorage = load(uuid);
        }
        return dataStorage;
    }

}
