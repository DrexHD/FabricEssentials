package me.drex.essentials;

import me.drex.essentials.util.AsyncChunkLoadUtil;
import me.drex.essentials.util.teleportation.Location;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import me.drex.essentials.command.CommandManager;
import me.drex.essentials.config.ConfigManager;
import me.drex.essentials.storage.DataStorage;
import me.drex.essentials.util.FabricEssentialsPlaceholders;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EssentialsMod implements ModInitializer {

    public static final String MOD_ID = "fabric-essentials";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ConfigManager.load();
        DataStorage.init();
        AsyncChunkLoadUtil.init();
        FabricEssentialsPlaceholders.register();
        CommandRegistrationCallback.EVENT.register(CommandManager::new);
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            CommandManager.dumpCommands(server.getCommands().getDispatcher(), server);
        });
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayer player) {
                DataStorage.updatePlayerData(player).saveLocation(new Location(player));
            }
        });
    }
}
