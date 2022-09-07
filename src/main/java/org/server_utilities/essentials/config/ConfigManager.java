package org.server_utilities.essentials.config;

import net.fabricmc.loader.api.FabricLoader;
import org.server_utilities.essentials.EssentialsMod;
import org.server_utilities.essentials.config.serializer.ResourceLocationSerializer;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;

public class ConfigManager {

    private static final Logger LOGGER = EssentialsMod.LOGGER;
    private static final String SUBDIRECTORY = "fabric-essentials";
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve(SUBDIRECTORY);
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("config.hocon");
    private EssentialsConfig essentialsConfig = new EssentialsConfig();

    public static final ConfigManager INSTANCE = new ConfigManager();

    public ConfigManager() {
        try {
            load();
        } catch (ConfigurateException e) {
            LOGGER.error("An error occurred while loading the config, keeping old values", e);
        }
    }

    public void load() throws ConfigurateException {
        LOGGER.info("Loading configuration...");
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().path(CONFIG_FILE).build();
        CommentedConfigurationNode rootNode = loader.load(ConfigurationOptions.defaults().serializers(builder -> builder.register(new ResourceLocationSerializer())));
        if (!CONFIG_FILE.toFile().exists()) {
            CONFIG_DIR.toFile().mkdirs();
            LOGGER.info("Creating configuration file!");
            rootNode.set(EssentialsConfig.class, new EssentialsConfig());
            loader.save(rootNode);
        }
        essentialsConfig = rootNode.get(EssentialsConfig.class, new EssentialsConfig());
    }

    public EssentialsConfig config() {
        return essentialsConfig;
    }

}
