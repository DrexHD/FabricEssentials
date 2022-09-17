package org.server_utilities.essentials.util.teleportation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.server_utilities.essentials.EssentialsMod;
import org.slf4j.Logger;

public class Location {

    private static final Logger LOGGER = EssentialsMod.LOGGER;
    private final Vec3 vec3;
    private final float yaw;
    private final float pitch;
    private final ResourceLocation dim;

    public Location(Vec3 location, float yaw, float pitch, ResourceLocation dim) {
        this.vec3 = location;
        this.yaw = yaw;
        this.pitch = pitch;
        this.dim = dim;
    }

    public Location(Entity entity) {
        this.vec3 = entity.position();
        this.yaw = entity.getYRot();
        this.pitch = entity.getXRot();
        this.dim = entity.getLevel().dimension().location();
    }

    public boolean teleport(@NotNull Entity entity) {
        if (entity.level.isClientSide) {
            return false;
        }

        BlockPos blockPos = new BlockPos(vec3);
        if (!Level.isInSpawnableBounds(blockPos)) {
            return false;
        }

        ServerLevel serverLevel = getLevel(entity.getServer());
        if (serverLevel == null) {
            LOGGER.warn("Can't teleport {} to {}, because dimension doesn't exist!", entity.getScoreboardName(), this);
            return false;
        }
        if (entity instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.connection == null) return false;
            ChunkPos chunkPos = new ChunkPos(blockPos);
            serverLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, entity.getId());
            entity.stopRiding();
            if (serverPlayer.isSleeping()) {
                serverPlayer.stopSleepInBed(true, true);
            }
            serverPlayer.teleportTo(serverLevel, vec3.x, vec3.y, vec3.z, yaw, pitch);
            entity.setYHeadRot(yaw);
        } else {
            entity.unRide();
            Entity originalEntity = entity;
            entity = originalEntity.getType().create(serverLevel);
            if (entity != null) {
                entity.restoreFrom(originalEntity);
                entity.moveTo(vec3.x, vec3.y, vec3.z, yaw, pitch);
                entity.setYHeadRot(yaw);
                originalEntity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                serverLevel.addDuringTeleport(entity);
            } else {
                return false;
            }
        }
        return true;
    }

    @Nullable
    public ServerLevel getLevel(MinecraftServer server) {
        return server.getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dim));
    }

    public ChunkPos getChunkPos() {
        return new ChunkPos((int) vec3.x >> 4, (int) vec3.z >> 4);
    }

    public Vec3 getVec3() {
        return vec3;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
