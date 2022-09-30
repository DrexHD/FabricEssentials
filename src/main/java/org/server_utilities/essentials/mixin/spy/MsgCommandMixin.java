package org.server_utilities.essentials.mixin.spy;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.util.KeyUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(MsgCommand.class)
public abstract class MsgCommandMixin {

    @Inject(method = "sendMessage", at = @At("HEAD"))
    private static void onMessage(CommandSourceStack src, Collection<ServerPlayer> receivers, MessageArgument.ChatMessage chatMessage, CallbackInfoReturnable<Integer> cir) {
        Component content = chatMessage.signedArgument().serverContent();
        MutableComponent spyMessage = Component.translatable(KeyUtil.translation("spy.socialspy"),
                src.getDisplayName(),
                ComponentUtils.formatList(receivers, Player::getDisplayName),
                content
        );
        for (ServerPlayer player : src.getServer().getPlayerList().getPlayers()) {
            if (DataStorage.STORAGE.getPlayerData(player).socialSpy && player != src.getPlayer() && !receivers.contains(player)) {
                player.sendSystemMessage(spyMessage);
            }
        }
    }

}
