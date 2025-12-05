package me.drex.essentials.util;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.resources.Identifier;
import me.drex.essentials.storage.DataStorage;
import me.drex.essentials.storage.PlayerData;
import me.drex.essentials.storage.ServerData;

import static me.drex.essentials.EssentialsMod.MOD_ID;

public class FabricEssentialsPlaceholders {

    private static Identifier location(String placeholderId) {
        return Identifier.fromNamespaceAndPath(MOD_ID, placeholderId);
    }

    public static void register() {
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
