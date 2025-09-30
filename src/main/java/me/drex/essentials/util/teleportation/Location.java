package me.drex.essentials.util.teleportation;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.drex.essentials.storage.DataStorage;
import me.drex.essentials.storage.PlayerData;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record Location(Vec3 pos, float yaw, float pitch, ResourceLocation dimension) {

    public Location(Entity entity) {
        this(entity.position(), entity.getYRot(), entity.getXRot(), entity.level().dimension().location());
    }

    public Location(CommandSourceStack source) {
        this(source.getPosition(), source.getRotation().y, source.getRotation().x, source.getLevel().dimension().location());
    }

    public boolean teleport(@NotNull Entity entity) {
        return teleport(entity, true);
    }

    public boolean teleport(@NotNull Entity entity, boolean saveLocation) {
        BlockPos blockPos = BlockPos.containing(pos);
        if (!Level.isInSpawnableBounds(blockPos)) {
            return false;
        } else {
            ServerLevel level = getLevel(entity.level().getServer());
            Location currentLocation = new Location(entity);
            if (entity.teleportTo(level, pos.x, pos.y, pos.z, Collections.emptySet(),  Mth.wrapDegrees(yaw),  Mth.wrapDegrees(pitch)/*? if >= 1.21.2 {*/, true /*?}*/)) {
                if (saveLocation && entity instanceof ServerPlayer player) {
                    PlayerData playerData = DataStorage.updatePlayerData(player);
                    playerData.saveLocation(currentLocation);
                }

                if (!(entity instanceof LivingEntity livingEntity) || !livingEntity.isFallFlying()) {
                    entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 0.0, 1.0));
                    entity.setOnGround(true);
                }

                if (entity instanceof PathfinderMob pathfinderMob) {
                    pathfinderMob.getNavigation().stop();
                }
            }
            return true;
        }
    }

    @Nullable
    public ServerLevel getLevel(@NotNull MinecraftServer server) {
        return server.getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
    }

    public ChunkPos chunkPos() {
        return new ChunkPos(SectionPos.blockToSectionCoord(pos.x), SectionPos.blockToSectionCoord(pos.z));
    }

    public Map<String, Component> placeholders() {
        return new HashMap<>() {{
            put("location_pos_x", Component.literal(String.format("%.2f", pos.x)));
            put("location_pos_y", Component.literal(String.format("%.2f", pos.y)));
            put("location_pos_z", Component.literal(String.format("%.2f", pos.z)));
            put("location_yaw", Component.literal(String.format("%.2f", yaw)));
            put("location_pitch", Component.literal(String.format("%.2f", pitch)));
            put("location_dimension", Component.literal(dimension.toString()));
        }};
    }


}
