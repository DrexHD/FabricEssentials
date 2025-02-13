package me.drex.essentials.config;

import me.drex.essentials.config.commands.CommandsConfig;
import me.drex.essentials.config.homes.HomesConfig;
import me.drex.essentials.config.teleportation.TeleportationConfig;
import me.drex.essentials.config.tpa.TpaConfig;

public class Config {

    public HomesConfig homes = new HomesConfig();

    public TpaConfig tpa = new TpaConfig();

    public CommandsConfig commands = new CommandsConfig();

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
