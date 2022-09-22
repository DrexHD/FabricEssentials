package org.server_utilities.essentials.util.teleportation;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
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

import java.util.Objects;

import static org.server_utilities.essentials.EssentialsMod.LOGGER;

public record Location(Vec3 pos, float yaw, float pitch, ResourceLocation dimension) {

    public Location(Entity entity) {
        this(entity.position(), entity.getYRot(), entity.getXRot(), entity.getLevel().dimension().location());
    }

    public Location(CommandSourceStack source) {
        this(source.getPosition(), source.getRotation().y, source.getRotation().x, source.getLevel().dimension().location());
    }

    public boolean teleport(@NotNull Entity entity) {
        if (entity.level.isClientSide) {
            return false;
        }

        BlockPos blockPos = new BlockPos(pos);
        if (!Level.isInSpawnableBounds(blockPos)) {
            return false;
        }

        ServerLevel serverLevel = getLevel(Objects.requireNonNull(entity.getServer()));
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
            serverPlayer.teleportTo(serverLevel, pos.x, pos.y, pos.z, yaw, pitch);
            entity.setYHeadRot(yaw);
        } else {
            entity.unRide();
            Entity originalEntity = entity;
            entity = originalEntity.getType().create(serverLevel);
            if (entity != null) {
                entity.restoreFrom(originalEntity);
                entity.moveTo(pos.x, pos.y, pos.z, yaw, pitch);
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
    public ServerLevel getLevel(@NotNull MinecraftServer server) {
        return server.getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dimension));
    }

    public ChunkPos chunkPos() {
        return new ChunkPos(SectionPos.blockToSectionCoord(pos.x), SectionPos.blockToSectionCoord(pos.z));
    }

}
