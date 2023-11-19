package org.server_utilities.essentials.config.commands;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class CommandsConfig {

    public ItemEditConfig itemEdit = new ItemEditConfig();

    @Comment("A list of commands that should be ignored by commandspy")
    public String[] ignoreCommandSpyCommands = new String[]{
        "me",
        "msg",
        "teammsg",
        "tell",
        "w"
    };

}
