package me.drex.essentials.mixin.async;

import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerChunkCache.class)
public interface IServerChunkCache {

    @Invoker
    boolean invokeRunDistanceManagerUpdates();

}
