package org.server_utilities.essentials;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.world.level.storage.LevelResource;
import org.server_utilities.essentials.command.CommandManager;
import org.server_utilities.essentials.config.EssentialsConfig;
import org.server_utilities.essentials.mixin.data.LevelResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EssentialsMod implements DedicatedServerModInitializer {

	public static final String MOD_ID = "fabric-essentials";
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final LevelResource ESSENTIALS_DATA_DIR = LevelResourceAccessor.init("essentials");
	private static EssentialsConfig essentialsConfig;

	/**
	 * Runs the mod initializer on the server environment.
	 */
	@Override
	public void onInitializeServer() {
		essentialsConfig = new EssentialsConfig();
		CommandRegistrationCallback.EVENT.register(CommandManager::new);
	}

	public static Logger getLogger() {
		return LOGGER;
	}

	public static EssentialsConfig getConfig() {
		return essentialsConfig;
	}

}
