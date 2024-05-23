package org.server_utilities.essentials.util;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.resources.ResourceLocation;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;
import org.server_utilities.essentials.storage.ServerData;

import static org.server_utilities.essentials.EssentialsMod.MOD_ID;

public class FabricEssentialsPlaceholders {

    private static ResourceLocation location(String placeholderId) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, placeholderId);
    }

    public static void register() {
        Placeholders.register(location("rtp_count"), (context, argument) -> {
            if (context.hasPlayer()) {
                PlayerData playerData = DataStorage.getPlayerData(context.player());
                return PlaceholderResult.value(String.valueOf(playerData.rtpCount));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });
        Placeholders.register(location("home_count"), (context, argument) -> {
            if (context.hasPlayer()) {
                PlayerData playerData = DataStorage.getPlayerData(context.player());
                return PlaceholderResult.value(String.valueOf(playerData.homes.size()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });
        Placeholders.register(location("warp_count"), (context, argument) -> {
            ServerData essentialsData = DataStorage.serverData();
            return PlaceholderResult.value(String.valueOf(essentialsData.getWarps().size()));
        });

    }

}
