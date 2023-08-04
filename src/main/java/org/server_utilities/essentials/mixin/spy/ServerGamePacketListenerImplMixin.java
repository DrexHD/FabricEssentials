package org.server_utilities.essentials.mixin.spy;

import eu.pb4.placeholders.api.PlaceholderContext;
import me.drex.message.api.Message;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.server_utilities.essentials.storage.DataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(method = "performChatCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performCommand(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;)I"))
    public void onCommand(ServerboundChatCommandPacket packet, LastSeenMessages lastSeenMessages, CallbackInfo ci) {
        MutableComponent spyMessage = Message.message("fabric-essentials.commandspy", new HashMap<>(){{
            put("command", Component.literal(packet.command()));
        }}, PlaceholderContext.of(player));
        for (ServerPlayer player : player.server.getPlayerList().getPlayers()) {
            if (DataStorage.STORAGE.getPlayerData(player).commandSpy && player != this.player) {
                player.sendSystemMessage(spyMessage);
            }
        }
    }

}
