package org.server_utilities.essentials.config;

import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandManager;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.config.homes.HomesConfig;
import org.server_utilities.essentials.config.rtp.RtpConfig;
import org.server_utilities.essentials.config.teleportation.TeleportationConfig;
import org.server_utilities.essentials.config.tpa.TpaConfig;

import java.util.HashMap;
import java.util.Map;

public class Config {

    public HomesConfig homes = new HomesConfig();

    public RtpConfig rtp = new RtpConfig();

    public TpaConfig tpa = new TpaConfig();

    public TeleportationConfig teleportation = new TeleportationConfig();

    public Map<String, CommandProperties> commands = new HashMap<>(){{
        for (Command command : CommandManager.COMMANDS) {
            put(command.defaultProperties().literal(), command.defaultProperties());
        }
    }};

    public ItemEditConfig itemEdit = new ItemEditConfig();

    public String[] ignoreCommandSpyCommands = new String[]{
        "me",
        "msg",
        "teammsg",
        "tell",
        "w"
    };

}
