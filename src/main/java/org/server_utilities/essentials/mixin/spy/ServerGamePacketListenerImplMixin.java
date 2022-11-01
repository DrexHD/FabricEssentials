package org.server_utilities.essentials.mixin.spy;

import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.util.KeyUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "performChatCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performCommand(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;)I"))
    public void onCommand(ServerboundChatCommandPacket packet, LastSeenMessages lastSeenMessages, CallbackInfo ci) {
        MutableComponent spyMessage = Component.translatable(KeyUtil.translation("spy.commandspy"),
                this.player.getDisplayName(),
                Component.literal(packet.command()).withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, packet.command())))
        );
        for (ServerPlayer player : this.server.getPlayerList().getPlayers()) {
            if (DataStorage.STORAGE.getPlayerData(player).commandSpy && player != this.player) {
                player.sendSystemMessage(spyMessage);
            }
        }
    }

}
