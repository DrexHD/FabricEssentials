package org.server_utilities.essentials.config;

public class ItemEditConfig {

    public NameConfig name = new NameConfig();

    public LoreConfig lore = new LoreConfig();

    public static class NameConfig {

        public int maxLength = 50;

        public int experienceLevelCost = 1;

    }

    public static class LoreConfig {

        public int maxLength = 50;

        public int maxLines = 5;

        public int experienceLevelCost = 1;

    }

}
