package org.server_utilities.essentials.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

public class TeleportationUtil {

    public static void teleportEntity(Entity target, ServerLevel targetLevel, BlockPos targetLocation) {
        int x = targetLocation.getX();
        int y = targetLocation.getY();
        int z = targetLocation.getZ();
        float yRot = target.getYRot();
        float xRot = target.getXRot();
        if (target instanceof ServerPlayer serverPlayer) {
            ChunkPos chunkPos = new ChunkPos(targetLocation);
            targetLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, serverPlayer.getId());
            serverPlayer.stopRiding();
            if (serverPlayer.isSleeping()) {
                serverPlayer.stopSleepInBed(true, true);
            }
            if (targetLevel == serverPlayer.level) {
                serverPlayer.connection.teleport(x + 0.5, y, z + 0.5, yRot, xRot);
            } else {
                serverPlayer.teleportTo(targetLevel, x + 0.5, y, z + 0.5, yRot, xRot);
            }
        } else {
            if (targetLevel == target.level) {
                target.moveTo(x, y, z, yRot, xRot);
            } else {
                target.unRide();
                Entity entity2 = target;
                target = target.getType().create(targetLevel);
                if (target == null) {
                    return;
                }
                target.restoreFrom(entity2);
                target.moveTo(x, y, z, yRot, xRot);
                entity2.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                targetLevel.addDuringTeleport(target);
            }
        }
    }

}
