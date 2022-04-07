package org.server_utilities.essentials.config.homes;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class HomesConfig {

    @Comment(value = "Default home limit")
    public int defaultLimit = 3;

    @Comment("Configurable home limits")
    public HomesLimit[] homesLimits = new HomesLimit[]{new HomesLimit()};

}
