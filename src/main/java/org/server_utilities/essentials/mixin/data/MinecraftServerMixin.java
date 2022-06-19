package org.server_utilities.essentials.mixin.data;

import com.mojang.datafixers.DataFixer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.server_utilities.essentials.storage.EssentialsDataStorage;
import org.server_utilities.essentials.util.data.ILevelStorageAccess;
import org.server_utilities.essentials.util.data.IMinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements IMinecraftServer {

    private EssentialsDataStorage essentialsDataStorage;

    @Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;createPlayerStorage()Lnet/minecraft/world/level/storage/PlayerDataStorage;"
            )
    )
    public void onInit(Thread thread, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, Proxy proxy, DataFixer dataFixer, Services services, ChunkProgressListenerFactory chunkProgressListenerFactory, CallbackInfo ci) {
        this.essentialsDataStorage = ((ILevelStorageAccess)levelStorageAccess).createEssentialsStorage();
    }

    @Inject(
            method = "saveAllChunks",
            at = @At("HEAD")
    )
    public void onSave(boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<Boolean> cir) {
        essentialsDataStorage.save();
    }

    @Override
    public EssentialsDataStorage getEssentialsStorage() {
        return essentialsDataStorage;
    }

}
