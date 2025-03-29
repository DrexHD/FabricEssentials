package me.drex.essentials.mixin.style;

import me.drex.essentials.util.StyledInputUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Function;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @ModifyArg(
        method = "method_33799",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/Filterable;map(Ljava/util/function/Function;)Lnet/minecraft/server/network/Filterable;"
        ),
        index = 0
    )
    public Function<String, Component> bookPageFormatting(Function<String, Component> original) {
        return input -> StyledInputUtil.parse(input, player.createCommandSourceStack(), "style.book.");
    }

}
