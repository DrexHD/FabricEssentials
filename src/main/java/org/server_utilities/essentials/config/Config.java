package org.server_utilities.essentials.config;

import org.server_utilities.essentials.config.homes.HomesConfig;
import org.server_utilities.essentials.config.teleportation.TeleportationConfig;
import org.server_utilities.essentials.config.tpa.TpaConfig;

public class Config {

    public HomesConfig homes = new HomesConfig();

    public TpaConfig tpa = new TpaConfig();

    public TeleportationConfig teleportation = new TeleportationConfig();

    public ItemEditConfig itemEdit = new ItemEditConfig();

    public String[] ignoreCommandSpyCommands = new String[]{
        "me",
        "msg",
        "teammsg",
        "tell",
        "w"
    };

}
