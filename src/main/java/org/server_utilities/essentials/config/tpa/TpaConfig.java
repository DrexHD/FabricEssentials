package org.server_utilities.essentials.config.tpa;

import org.server_utilities.essentials.config.util.WaitingPeriodConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class TpaConfig {

    @Comment("Define a waiting period to prevent players from using tpas to escape dangerous situations")
    public WaitingPeriodConfig waitingPeriod = new WaitingPeriodConfig();

    @Comment("Amount of seconds until a tpa request expires")
    public int expiry = 30;

}
