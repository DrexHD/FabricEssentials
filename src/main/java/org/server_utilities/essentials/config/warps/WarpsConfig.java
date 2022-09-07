package org.server_utilities.essentials.config.warps;

import org.server_utilities.essentials.config.util.WaitingPeriodConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class WarpsConfig {

    @Comment("Define a waiting period to prevent players from using warps to escape dangerous situations")
    public WaitingPeriodConfig waitingPeriod = new WaitingPeriodConfig();

}
