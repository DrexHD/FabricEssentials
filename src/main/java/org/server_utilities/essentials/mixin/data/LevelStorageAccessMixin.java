package org.server_utilities.essentials.mixin.data;

import net.minecraft.world.level.storage.LevelStorageSource;
import org.server_utilities.essentials.storage.EssentialsDataStorage;
import org.server_utilities.essentials.util.data.ILevelStorageAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelStorageSource.LevelStorageAccess.class)
public abstract class LevelStorageAccessMixin implements ILevelStorageAccess {

    @Shadow
    protected abstract void checkLock();

    @SuppressWarnings("ShadowTarget")
    @Final
    @Shadow
    LevelStorageSource field_23766; // final synthetic Lnet/minecraft/world/level/storage/LevelStorageSource; field_23766

    @Override
    public EssentialsDataStorage createEssentialsStorage() {
        this.checkLock();
        return new EssentialsDataStorage((LevelStorageSource.LevelStorageAccess) (Object) this, ((LevelStorageSourceAccessor) field_23766).getFixerUpper()); // return new EssentialsDataStorage(this, LevelStorageSource.this.fixerUpper)
    }
}
