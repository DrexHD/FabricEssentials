package org.server_utilities.essentials;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.JsonDataStorage;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.server_utilities.essentials.command.CommandManager;
import org.server_utilities.essentials.config.ConfigManager;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.EssentialsData;
import org.server_utilities.essentials.storage.PlayerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;

public class EssentialsMod implements DedicatedServerModInitializer {

    public static final String MOD_ID = "fabric-essentials";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final JsonDataStorage<PlayerData> USER_DATA_STORAGE = new JsonDataStorage<>("fabric-essentials", PlayerData.class, DataStorage.GSON);
    public static final JsonDataStorage<EssentialsData> ESSENTIALS_DATA_STORAGE = new JsonDataStorage<>("fabric-essentials-global", EssentialsData.class, DataStorage.GSON);

    /**
     * Runs the mod initializer on the server environment.
     */
    @Override
    public void onInitializeServer() {
        try {
            ConfigManager.INSTANCE.load();
        } catch (ConfigurateException e) {
            LOGGER.error("An error occurred while loading the config, keeping default values", e);
        }
        PlayerDataApi.register(USER_DATA_STORAGE);
        PlayerDataApi.register(ESSENTIALS_DATA_STORAGE);
        CommandRegistrationCallback.EVENT.register(CommandManager::new);
    }

}
