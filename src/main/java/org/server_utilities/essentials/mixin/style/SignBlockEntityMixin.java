package org.server_utilities.essentials.mixin.style;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.server_utilities.essentials.util.IdentifierUtil;
import org.server_utilities.essentials.util.StyledInputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin {

    @Redirect(
            method = "setMessages",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"
            )
    )
    public MutableComponent signFormatting(String input, Player player) {
        MutableComponent formatted = (MutableComponent) StyledInputUtil.parse(input, textTag -> IdentifierUtil.check(player, "style.sign" + textTag.name()));
        // This check is required to keep signs editable, which rely on literal text
        if (formatted.getString().equals(input)) {
            return Component.literal(input);
        }
        return formatted;
    }

}
