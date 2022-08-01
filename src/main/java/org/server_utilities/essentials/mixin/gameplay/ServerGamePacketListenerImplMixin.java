package org.server_utilities.essentials.mixin.gameplay;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.util.StyledInputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;
import java.util.function.UnaryOperator;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @Redirect(method = "updateSignText", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/FilteredText;map(Ljava/util/function/Function;)Lnet/minecraft/server/network/FilteredText;"))
    public FilteredText<Component> signFormatting(FilteredText<String> filteredText, Function<String, Component> function) {
        return filteredText.map(input -> StyledInputUtil.parse(input, textTag -> Command.permission("style", "sign", "format", textTag.name()).test(player.createCommandSourceStack())));
    }

    @ModifyArg(method = "signBook", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;updateBookPages(Ljava/util/List;Ljava/util/function/UnaryOperator;Lnet/minecraft/world/item/ItemStack;)V"), index = 1)
    public UnaryOperator<String> bookPageFormatting(UnaryOperator<String> original) {
        return input -> Component.Serializer.toJson(StyledInputUtil.parse(input, textTag -> Command.permission("style", "book", "format", textTag.name()).test(player.createCommandSourceStack())));
    }

}
