package org.server_utilities.essentials.mixin.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.server_utilities.essentials.util.data.IMinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(
            method = "save",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/PlayerDataStorage;save(Lnet/minecraft/world/entity/player/Player;)V"
            )
    )
    public void onSave(ServerPlayer serverPlayer, CallbackInfo ci) {
        ((IMinecraftServer) this.server).getEssentialsStorage().save((PlayerList) (Object) this, serverPlayer.getUUID());
    }

    @Inject(
            method = "load",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/PlayerDataStorage;load(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/nbt/CompoundTag;"
            )
    )
    public void onLoad(ServerPlayer serverPlayer, CallbackInfoReturnable<CompoundTag> cir) {
        ((IMinecraftServer) this.server).getEssentialsStorage().load(serverPlayer.getUUID());
    }

}
