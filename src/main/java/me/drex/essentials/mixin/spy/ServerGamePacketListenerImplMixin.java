package me.drex.essentials.mixin.spy;

import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import me.drex.essentials.config.ConfigManager;
import me.drex.essentials.storage.DataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

import static me.drex.message.api.LocalizedMessage.localized;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(
        method = "performUnsignedChatCommand",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/commands/Commands;performCommand(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;)V"
        )
    )
    public void onUnsignedCommand(String command, CallbackInfo ci) {
        handleCommand(command);
    }

    @Inject(
        method = "performSignedChatCommand",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/commands/Commands;performCommand(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;)V"
        )
    )
    public void onSignedCommand(ServerboundChatCommandSignedPacket packet, LastSeenMessages lastSeenMessages, CallbackInfo ci) {
        handleCommand(packet.command());
    }

    @Unique
    private void handleCommand(String command) {
        for (String ignoreCommandSpyCommand : ConfigManager.config().ignoreCommandSpyCommands) {
            if (command.startsWith(ignoreCommandSpyCommand)) return;
        }
        MutableComponent spyMessage = localized("fabric-essentials.commandspy", new HashMap<>(){{
            put("command", Component.literal(command));
        }}, PlaceholderContext.of(player));
        for (ServerPlayer player : player.getServer().getPlayerList().getPlayers()) {
            if (DataStorage.getPlayerData(player).commandSpy && player != this.player) {
                player.sendSystemMessage(spyMessage);
            }
        }
    }

}
