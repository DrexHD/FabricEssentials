package org.server_utilities.essentials.config.misc;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.Set;

@ConfigSerializable
public class MiscConfig {

    @Comment("A list of time durations that should be broadcast in chat messages")
    public Set<Long> announcedTimeDuration = Set.of(30L, 15L, 10L, 5L, 4L, 3L, 2L, 1L);

}
