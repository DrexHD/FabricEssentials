package org.server_utilities.essentials;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.level.storage.LevelResource;
import org.server_utilities.essentials.command.Manager;
import org.server_utilities.essentials.mixin.data.LevelResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EssentialsMod implements DedicatedServerModInitializer {

	public static final String MOD_ID = "fabric-essentials";
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final LevelResource ESSENTIALS_DATA_DIR = LevelResourceAccessor.init("essentials");

	/**
	 * Runs the mod initializer on the server environment.
	 */
	@Override
	public void onInitializeServer() {
		CommandRegistrationCallback.EVENT.register(Manager::new);
	}

	public static Logger getLogger() {
		return LOGGER;
	}

}
