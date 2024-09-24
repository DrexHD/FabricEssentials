package me.drex.essentials.mixin.event;

import net.minecraft.server.players.PlayerList;
import me.drex.essentials.storage.DataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Inject(method = "saveAll", at = @At(value = "HEAD"))
    public void onSave(CallbackInfo ci) {
        DataStorage.save();
    }

}
