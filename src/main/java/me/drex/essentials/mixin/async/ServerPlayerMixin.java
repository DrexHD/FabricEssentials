package me.drex.essentials.mixin.async;

import com.mojang.authlib.GameProfile;
import me.drex.essentials.config.teleportation.WaitingPeriodConfig;
import me.drex.essentials.util.AsyncTeleportPlayer;
import me.drex.essentials.util.IdentifierUtil;
import me.drex.essentials.util.TeleportCancelException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import static me.drex.essentials.config.teleportation.WaitingPeriodConfig.WaitingResult.*;
import static me.drex.message.api.LocalizedMessage.localized;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements AsyncTeleportPlayer {
    //? if >= 1.21.6-rc1 {
    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }
    //? } else {
    /*public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }*/
    //? }

    @Shadow
    public abstract void sendSystemMessage(Component component);

    @Shadow
    @Final
    MinecraftServer server;

    @Shadow
    public abstract void sendSystemMessage(Component component, boolean bl);

    private CompletableFuture<Void> waitingPeriodFuture = CompletableFuture.completedFuture(null);
    private int waitingPeriodTicks = 0;
    @Nullable
    private WaitingPeriodConfig waitingPeriodConfig = null;
    private boolean asyncLoadingChunks = false;
    private CommandSourceStack waitingPeriodSource = null;

    @Override
    public CompletableFuture<Void> delayedTeleport(@NotNull CommandSourceStack originalSource, @NotNull WaitingPeriodConfig context) {
        return delayedTeleport(originalSource, context.period * 20, context);
    }

    @Override
    public CompletableFuture<Void> delayedTeleport(@NotNull CommandSourceStack originalSource, int waitingTicks, @Nullable WaitingPeriodConfig context) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        waitingPeriodFuture = future;
        waitingPeriodTicks = waitingTicks;
        waitingPeriodConfig = context;
        waitingPeriodSource = originalSource;
        return future;
    }

    @Override
    public void cancelDelayedTeleport(TeleportCancelException cancelException) {
        waitingPeriodTicks = -1;
        waitingPeriodSource = null;
        waitingPeriodFuture.completeExceptionally(cancelException);
    }

    @Override
    public boolean isAsyncLoadingChunks() {
        return asyncLoadingChunks;
    }

    @Override
    public void setAsyncLoadingChunks(boolean loadingChunks) {
        asyncLoadingChunks = loadingChunks;
        if (!loadingChunks) {
            this.sendSystemMessage(Component.empty(), true);
            this.sendSystemMessage(localized("fabric-essentials.async.loading_chunks.done"), true);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        if (isAsyncLoadingChunks()) {
            String key = String.valueOf((this.server.getTickCount() / 6) % 3);
            this.sendSystemMessage(localized("fabric-essentials.async.loading_chunks." + key), true);
        }
        if (waitingPeriodTicks >= 0) {
            if (waitingPeriodTicks == 0) {
                waitingPeriodFuture.complete(null);
            } else {
                if (waitingPeriodConfig != null) {
                    WaitingPeriodConfig.CancellationConfig cancellation = waitingPeriodConfig.cancellation;
                    double distance = waitingPeriodSource.getPosition().distanceTo(this.position());
                    ServerPlayer player = (ServerPlayer) (Object) this;
                    if (cancellation.maxMoveDistance >= 0 && distance >= cancellation.maxMoveDistance && !IdentifierUtil.check(player, "teleport.cancel.bypass.move")) {
                        cancelDelayedTeleport(new TeleportCancelException(MOVE.component()));
                        return;
                    }
                    if (cancellation.damage && this.getLastDamageSource() != null && !IdentifierUtil.check(player, "teleport.cancel.bypass.damage")) {
                        cancelDelayedTeleport(new TeleportCancelException(DAMAGE.component()));
                        return;
                    }
                    if (this.server.getPlayerList().getPlayer(this.getUUID()) == null) {
                        cancelDelayedTeleport(new TeleportCancelException(UNKNOWN.component()));
                        return;
                    }
                }
                if (waitingPeriodTicks % 20 == 0) {
                    int ceilDiv = -Math.floorDiv(-waitingPeriodTicks, 20);
                    this.sendSystemMessage(localized("fabric-essentials.teleport.wait", new HashMap<>() {{
                        put("time", Component.literal(String.valueOf(ceilDiv)));
                    }}));
                }
            }
            waitingPeriodTicks--;
        }
    }

}
