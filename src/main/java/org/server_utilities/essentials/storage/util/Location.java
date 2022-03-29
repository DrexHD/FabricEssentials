package org.server_utilities.essentials.storage.util;

import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
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

    private static final Logger LOGGER = EssentialsMod.getLogger();
    private Vec3 location;
    private float yaw, pitch;
    private ResourceKey<Level> dimension = Level.OVERWORLD;

    public Location(Vec3 location, float yaw, float pitch, ResourceKey<Level> dimension) {
        this.location = location;
        this.yaw = yaw;
        this.pitch = pitch;
        this.dimension = dimension;
    }

    public Location(Entity entity) {
        this.location = entity.position();
        this.yaw = entity.getYRot();
        this.pitch = entity.getXRot();
        this.dimension = entity.getLevel().dimension();
    }

    public Location(CompoundTag compoundTag) {
        try {
            load(compoundTag);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Loading location NBT");
            throw new ReportedException(crashReport);
        }
    }

    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.put("Pos", this.newDoubleList(location.x, location.y, location.z));
        compoundTag.put("Rotation", this.newFloatList(yaw, pitch));
        ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, dimension.location()).resultOrPartial(LOGGER::error).ifPresent(tag -> compoundTag.put("Dimension", tag));
        ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, dimension.location()).resultOrPartial(LOGGER::error).ifPresent(tag -> compoundTag.put("Dimension", tag));
        return compoundTag;
    }

    public void load(CompoundTag compoundTag) {
        ListTag pos = compoundTag.getList("Pos", Tag.TAG_DOUBLE);
        ListTag rotation = compoundTag.getList("Rotation", Tag.TAG_FLOAT);
        location = new Vec3(pos.getDouble(0), pos.getDouble(1), pos.getDouble(2));
        yaw = rotation.getFloat(0);
        pitch = rotation.getFloat(1);
        if (compoundTag.contains("Dimension")) {
            this.dimension = Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, compoundTag.get("Dimension")).resultOrPartial(LOGGER::error).orElse(Level.OVERWORLD);
        }
    }

    protected ListTag newDoubleList(double... doubles) {
        ListTag listTag = new ListTag();
        for (double d : doubles) {
            listTag.add(DoubleTag.valueOf(d));
        }
        return listTag;
    }

    protected ListTag newFloatList(float... floats) {
        ListTag listTag = new ListTag();
        for (float f : floats) {
            listTag.add(FloatTag.valueOf(f));
        }
        return listTag;
    }

    public boolean teleport(@NotNull Entity entity) {
        if (entity.level.isClientSide) {
            return false;
        }
        BlockPos blockPos = new BlockPos(getX(), getY(), getZ());
        if (!Level.isInSpawnableBounds(blockPos)) {
            return false;
        }

        ServerLevel serverLevel = entity.getServer().getLevel(this.dimension);
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

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    @Override
    public String toString() {
        return "Location{" +
                "location=" + location +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                ", dimension=" + dimension +
                '}';
    }
}
