package org.server_utilities.essentials;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.JsonDataStorage;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.server_utilities.essentials.command.CommandManager;
import org.server_utilities.essentials.config.EssentialsConfig;
import org.server_utilities.essentials.storage.EssentialsData;
import org.server_utilities.essentials.storage.PlayerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Color on signs / anvil
public class EssentialsMod implements DedicatedServerModInitializer {

	public static final String MOD_ID = "fabric-essentials";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static EssentialsConfig essentialsConfig;
	public static final JsonDataStorage<PlayerData> USER_DATA_STORAGE = new JsonDataStorage<>("fabric-essentials", PlayerData.class);
	public static final JsonDataStorage<EssentialsData> ESSENTIALS_DATA_STORAGE = new JsonDataStorage<>("fabric-essentials", EssentialsData.class);

	/**
	 * Runs the mod initializer on the server environment.
	 */
	@Override
	public void onInitializeServer() {
		essentialsConfig = new EssentialsConfig();
		PlayerDataApi.register(USER_DATA_STORAGE);
		PlayerDataApi.register(ESSENTIALS_DATA_STORAGE);
		CommandRegistrationCallback.EVENT.register(CommandManager::new);
	}

	public static EssentialsConfig getConfig() {
		return essentialsConfig;
	}

}
