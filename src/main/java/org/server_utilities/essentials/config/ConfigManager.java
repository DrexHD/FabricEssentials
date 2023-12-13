package org.server_utilities.essentials.config;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import eu.pb4.playerdata.impl.BaseGson;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.server_utilities.essentials.EssentialsMod.LOGGER;

public class ConfigManager {

    public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    public static final Path CONFIG_FILE = CONFIG_DIR.resolve("fabric-essentials.json");
    private static final Gson GSON = BaseGson.createBuilder().setPrettyPrinting().create();
    private static Config config = new Config();

    public static boolean load() {
        LOGGER.info("Loading essentials config");
        if (Files.exists(CONFIG_FILE)) {
            try {
                String data = Files.readString(CONFIG_FILE);
                try {
                    config = GSON.fromJson(data, Config.class);
                    return true;
                } catch (JsonSyntaxException e) {
                    LOGGER.error("Failed to parse essentials config", e);
                }
            } catch (IOException e) {
                LOGGER.error("Failed to load essentials config", e);
            }
        } else {
            try {
                Files.writeString(CONFIG_FILE, GSON.toJson(config));
                return true;
            } catch (IOException e) {
                LOGGER.error("Failed to save essentials config", e);
            }
        }
        return false;
    }

    public static Config config() {
        return config;
    }

}
