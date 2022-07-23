package org.server_utilities.essentials.util.teleportation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.server_utilities.essentials.EssentialsMod;
import org.slf4j.Logger;

public class Location {

    private static final Logger LOGGER = EssentialsMod.LOGGER;
    private Vec3 location;
    private float yaw, pitch;
    private ResourceLocation dim;

    public Location(Vec3 location, float yaw, float pitch, ResourceLocation dim) {
        this.location = location;
        this.yaw = yaw;
        this.pitch = pitch;
        this.dim = dim;
    }

    public Location(Entity entity) {
        this.location = entity.position();
        this.yaw = entity.getYRot();
        this.pitch = entity.getXRot();
        this.dim = entity.getLevel().dimension().location();
    }

    public boolean teleport(@NotNull Entity entity) {
        if (entity.level.isClientSide) {
            return false;
        }
        BlockPos blockPos = new BlockPos(getX(), getY(), getZ());
        if (!Level.isInSpawnableBounds(blockPos)) {
            return false;
        }

        ServerLevel serverLevel = entity.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dim));
        if (serverLevel == null) {
            LOGGER.warn("Can't teleport {} to {}, because dimension doesn't exist!", entity.getScoreboardName(), this);
            return false;
        }
        double x = getX();
        double y = getY();
        double z = getZ();
        if (entity instanceof ServerPlayer serverPlayer) {
            ChunkPos chunkPos = new ChunkPos(blockPos);
            serverLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, entity.getId());
            entity.stopRiding();
            if (serverPlayer.isSleeping()) {
                serverPlayer.stopSleepInBed(true, true);
            }
            serverPlayer.teleportTo(serverLevel, x, y, z, yaw, pitch);
            entity.setYHeadRot(yaw);
        } else {
            entity.unRide();
            Entity originalEntity = entity;
            entity = originalEntity.getType().create(serverLevel);
            if (entity != null) {
                entity.restoreFrom(originalEntity);
                entity.moveTo(x, y, z, yaw, pitch);
                entity.setYHeadRot(yaw);
                originalEntity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                serverLevel.addDuringTeleport(entity);
            } else {
                return false;
            }
        }
        return true;
    }

    public double getX() {
        return location.x;
    }

    public double getY() {
        return location.y;
    }

    public double getZ() {
        return location.z;
    }

    public Vec3 getLocation() {
        return location;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
