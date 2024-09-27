package me.drex.essentials.mixin;

import me.drex.essentials.storage.DataStorage;
import me.drex.essentials.storage.PlayerData;
import me.drex.essentials.util.teleportation.Location;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(TeleportCommand.class)
public class TeleportCommandMixin {

    @Inject(
        method = "performTeleport",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z"
        )
    )
    private static void onTeleport(CommandSourceStack commandSourceStack, Entity entity, ServerLevel serverLevel, double d, double e, double f, Set<RelativeMovement> set, float g, float h, @Coerce Object lookAt, CallbackInfo ci) {
        CommandSource source = ((CommandSourceStackAccessor) commandSourceStack).getSource();
        if (source instanceof ServerPlayer && entity instanceof ServerPlayer player) {
            // The command was actually executed by a player and not *as* a player (e.g. from /execute as)
            // And the affected entity was also a player
            PlayerData playerData = DataStorage.updatePlayerData(player);
            // Save location before teleportation
            playerData.saveLocation(new Location(player));
        }
    }

}
