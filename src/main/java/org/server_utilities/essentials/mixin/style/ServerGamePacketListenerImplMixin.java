package org.server_utilities.essentials.mixin.style;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.server_utilities.essentials.util.IdentifierUtil;
import org.server_utilities.essentials.util.StyledInputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.UnaryOperator;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @ModifyArg(
            method = "signBook",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;updateBookPages(Ljava/util/List;Ljava/util/function/UnaryOperator;Lnet/minecraft/world/item/ItemStack;)V"
            ),
            index = 1
    )
    public UnaryOperator<String> bookPageFormatting(UnaryOperator<String> original) {
        return input -> Component.Serializer.toJson(StyledInputUtil.parse(input, textTag -> IdentifierUtil.check(player, "style.book." + textTag.name())));
    }

}
