package me.drex.essentials.mixin.style;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.drex.essentials.util.StyledInputUtil;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin {

    @WrapOperation(
        method = "setMessages",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"
        )
    )
    public MutableComponent signFormatting(String input, Operation<MutableComponent> original, @Local(argsOnly = true) Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            MutableComponent formatted = (MutableComponent) StyledInputUtil.parse(input, serverPlayer.createCommandSourceStack(), "style.sign.");
            // This check is required to keep signs editable, which rely on literal text
            if (!formatted.getString().equals(input)) {
                return formatted;
            }
        }
        return original.call(input);
    }

}
