package org.server_utilities.essentials.config.homes;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class HomesLimit {

    @Comment(value = "The permission value required to get extra / limited amount of homes. Permission will be: fabric_essentials.sethome.limit.<permission>")
    public String permission = "vip";

    @Comment(value = "Custom home limit")
    public int limit = 5;

    @Comment(value = "Whether or not this limit should increase or set the max limit")
    public boolean stacks = false;

}
