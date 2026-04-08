package me.drex.essentials.mixin.menu;

import me.drex.essentials.util.menu.DummyEnchantmentMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {
    @ModifyArg(
        method = "lambda$slotsChanged$0",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentCost(Lnet/minecraft/util/RandomSource;IILnet/minecraft/world/item/ItemStack;)I"
        ),
        index = 2
    )
    public int changeBookShelfCount(int original) {
        if ((Object) this instanceof DummyEnchantmentMenu dummyEnchantmentMenu) {
            return dummyEnchantmentMenu.bookCases;
        }
        return original;
    }
}
