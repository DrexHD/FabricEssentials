package me.drex.essentials.item;

import me.drex.essentials.EssentialsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {

    public static final TagKey<Item> HAT_DENY = bind("hat_deny");

    private static TagKey<Item> bind(String string) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(EssentialsMod.MOD_ID, string));
    }

}
