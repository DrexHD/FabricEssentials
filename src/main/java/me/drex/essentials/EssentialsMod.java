package me.drex.essentials;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import me.drex.essentials.command.CommandManager;
import me.drex.essentials.config.ConfigManager;
import me.drex.essentials.storage.DataStorage;
import me.drex.essentials.util.FabricEssentialsPlaceholders;
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
