package org.server_utilities.essentials.config;

import org.server_utilities.essentials.config.homes.HomesConfig;
import org.server_utilities.essentials.config.rtp.RtpConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class MainConfig {

    @Setting("home")
    public HomesConfig homesConfig = new HomesConfig();

    @Setting("rtp")
    public RtpConfig rtpConfig = new RtpConfig();

}
