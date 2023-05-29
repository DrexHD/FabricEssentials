package org.server_utilities.essentials.mixin.style;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import org.server_utilities.essentials.util.IdentifierUtil;
import org.server_utilities.essentials.util.StyledInputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    public AnvilMenuMixin(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(menuType, i, inventory, containerLevelAccess);
    }

    @Redirect(
            method = "createResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"
            )
    )
    public MutableComponent itemNameFormatting(String input) {
        MutableComponent formatted = (MutableComponent) StyledInputUtil.parse(input, textTag -> IdentifierUtil.check(this.player, "style.anvil." + textTag.name()));
        // This check is required to stay compatible with datapacks, which rely on vanilla text formatting
        if (formatted.getString().equals(input)) {
            return Component.literal(input);
        } else {
            return formatted;
        }
    }

}
