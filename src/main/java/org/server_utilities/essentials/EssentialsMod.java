package org.server_utilities.essentials;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.server_utilities.essentials.command.CommandManager;
import org.server_utilities.essentials.config.ConfigManager;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.util.FabricEssentialsPlaceholders;
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
        ConfigManager.load();
        DataStorage.init();
        FabricEssentialsPlaceholders.register();
        CommandRegistrationCallback.EVENT.register(CommandManager::new);
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            CommandManager.dumpCommands(server.getCommands().getDispatcher(), server);
        });
    }

}
