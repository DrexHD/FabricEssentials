package me.drex.essentials.datagen;

import me.drex.essentials.item.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
//? if >= 26.2 {
import net.minecraft.references.BlockItemIds;
//? } else {
//import net.minecraft.world.item.Items;
//? }

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        //? if >= 26.2 {
        this.builder(ModItemTags.HAT_DENY)
            .add(BlockItemIds.BEDROCK);
        //? } else {
        /*this.valueLookupBuilder(ModItemTags.HAT_DENY)
            .add(Items.BEDROCK);*/
        //? }
    }
}
