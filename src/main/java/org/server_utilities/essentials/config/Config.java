package org.server_utilities.essentials.config;

import org.server_utilities.essentials.config.commands.CommandsConfig;
import org.server_utilities.essentials.config.homes.HomesConfig;
import org.server_utilities.essentials.config.rtp.RtpConfig;
import org.server_utilities.essentials.config.teleportation.TeleportationConfig;
import org.server_utilities.essentials.config.tpa.TpaConfig;

public class Config {

    public HomesConfig homes = new HomesConfig();

    public RtpConfig rtp = new RtpConfig();

    public TpaConfig tpa = new TpaConfig();

    public TeleportationConfig teleportation = new TeleportationConfig();

    public CommandsConfig commands = new CommandsConfig();

    public ItemEditConfig itemEdit = new ItemEditConfig();

    public String[] ignoreCommandSpyCommands = new String[]{
        "me",
        "msg",
        "teammsg",
        "tell",
        "w"
    };

}
