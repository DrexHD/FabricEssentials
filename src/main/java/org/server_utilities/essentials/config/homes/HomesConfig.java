package org.server_utilities.essentials.config.homes;

import org.server_utilities.essentials.config.util.WaitingPeriodConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class HomesConfig {

    @Comment(value = "Default home limit")
    public int defaultLimit = 3;

    @Comment("Define a waiting period to prevent players from using homes to escape dangerous situations")
    public WaitingPeriodConfig waitingPeriod = new WaitingPeriodConfig();

    @Comment("Configurable home limits")
    public HomesLimit[] homesLimits = new HomesLimit[]{new HomesLimit()};

}
