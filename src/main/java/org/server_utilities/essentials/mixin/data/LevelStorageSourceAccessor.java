package org.server_utilities.essentials.mixin.data;

import com.mojang.datafixers.DataFixer;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelStorageSource.class)
public interface LevelStorageSourceAccessor {

    @Accessor
    DataFixer getFixerUpper();

}
