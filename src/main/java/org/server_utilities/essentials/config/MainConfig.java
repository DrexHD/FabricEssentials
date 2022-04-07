package org.server_utilities.essentials.config;

import org.server_utilities.essentials.config.homes.HomesConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class MainConfig {

    @Setting("homes")
    public HomesConfig homesConfig = new HomesConfig();

}
