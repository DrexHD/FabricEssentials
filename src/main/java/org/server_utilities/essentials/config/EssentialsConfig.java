package org.server_utilities.essentials.config;

import org.server_utilities.essentials.config.homes.HomesConfig;
import org.server_utilities.essentials.config.misc.MiscConfig;
import org.server_utilities.essentials.config.rtp.RtpConfig;
import org.server_utilities.essentials.config.warps.WarpsConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class EssentialsConfig {

    public HomesConfig homes = new HomesConfig();
    public WarpsConfig warps = new WarpsConfig();
    public RtpConfig rtp = new RtpConfig();
    public MiscConfig misc = new MiscConfig();

}
