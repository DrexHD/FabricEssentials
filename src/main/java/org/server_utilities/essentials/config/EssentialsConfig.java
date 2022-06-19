package org.server_utilities.essentials.config;

import net.fabricmc.loader.api.FabricLoader;
import org.server_utilities.essentials.EssentialsMod;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;

public class EssentialsConfig {

    private static final Logger LOGGER = EssentialsMod.getLogger();
    private static final String SUBDIRECTORY = "fabric-essentials";
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve(SUBDIRECTORY);
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("config.hocon");
    private MainConfig mainConfig = new MainConfig();

    public EssentialsConfig() {
        try {
            load();
        } catch (ConfigurateException e) {
            LOGGER.error("An error occurred while loading the config, keeping old values", e);
        }
    }

    public void load() throws ConfigurateException {
        LOGGER.info("Loading configuration...");
        HoconConfigurationLoader configurationLoader = HoconConfigurationLoader.builder().path(CONFIG_FILE).build();
        CommentedConfigurationNode configurationNode = configurationLoader.load();
        if (!CONFIG_FILE.toFile().exists()) {
            CONFIG_DIR.toFile().mkdirs();
            configurationLoader.save(configurationNode);
        }
        mainConfig = configurationNode.get(MainConfig.class, new MainConfig());
    }

    public MainConfig main() {
        return mainConfig;
    }

}
