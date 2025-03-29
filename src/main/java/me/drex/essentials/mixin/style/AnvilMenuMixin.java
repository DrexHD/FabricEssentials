package me.drex.essentials.mixin.style;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.essentials.util.StyledInputUtil;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    public AnvilMenuMixin(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess, ItemCombinerMenuSlotDefinition itemCombinerMenuSlotDefinition) {
        super(menuType, i, inventory, containerLevelAccess, itemCombinerMenuSlotDefinition);
    }

    @WrapOperation(
        method = "createResult",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"
        )
    )
    public MutableComponent itemNameFormatting(String input, Operation<MutableComponent> original) {
        if (this.player instanceof ServerPlayer serverPlayer) {
            MutableComponent formatted = (MutableComponent) StyledInputUtil.parse(input, serverPlayer.createCommandSourceStack(), "style.anvil.");
            // This check is required to stay compatible with datapacks, which rely on vanilla text formatting
            if (!formatted.getString().equals(input)) {
                return formatted;
            }
        }
        return original.call(input);
    }

}
