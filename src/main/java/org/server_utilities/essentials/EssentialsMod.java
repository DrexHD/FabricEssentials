package org.server_utilities.essentials;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.server_utilities.essentials.command.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EssentialsMod implements DedicatedServerModInitializer {

	public static final String MOD_ID = "fabric-essentials";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/**
	 * Runs the mod initializer on the server environment.
	 */
	@Override
	public void onInitializeServer() {
		CommandRegistrationCallback.EVENT.register(Manager::new);
	}
}
