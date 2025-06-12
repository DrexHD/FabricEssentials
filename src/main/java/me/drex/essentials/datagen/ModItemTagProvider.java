package me.drex.essentials.datagen;

import me.drex.essentials.item.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        //? if >= 1.21.6-rc1 {
        this.valueLookupBuilder(ModItemTags.HAT_DENY)
        //? } else {
        /*this.getOrCreateTagBuilder(ModItemTags.HAT_DENY)*/
        //? }
            .add(Items.BEDROCK);
    }
}
