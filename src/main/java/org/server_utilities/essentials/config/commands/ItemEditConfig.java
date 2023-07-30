package org.server_utilities.essentials.config.commands;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class ItemEditConfig {

    public NameConfig name = new NameConfig();

    public LoreConfig lore = new LoreConfig();

    @ConfigSerializable
    public static class NameConfig {

        public int maxLength = 50;

        public int experienceLevelCost = 1;

    }

    @ConfigSerializable
    public static class LoreConfig {

        public int maxLength = 50;

        public int maxLines = 5;

        public int experienceLevelCost = 1;

    }

}
