package me.drex.essentials.mixin.style;

import me.drex.essentials.util.StyledInputUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SignBlockEntity;
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
        MutableComponent formatted = (MutableComponent) StyledInputUtil.parse(input, ((ServerPlayer) player).createCommandSourceStack(), "style.sign.");
        // This check is required to keep signs editable, which rely on literal text
        if (formatted.getString().equals(input)) {
            return Component.literal(input);
        }
        return formatted;
    }

}
