package org.server_utilities.essentials.util;

import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.server_utilities.essentials.config.teleportation.WaitingPeriodConfig;

import java.util.concurrent.CompletableFuture;

public interface AsyncTeleportPlayer {

    CompletableFuture<Void> delayedTeleport(@NotNull CommandSourceStack originalSource, @NotNull WaitingPeriodConfig context);

    CompletableFuture<Void> delayedTeleport(@NotNull CommandSourceStack originalSource, int waitingTicks, @Nullable WaitingPeriodConfig context);

    void cancelDelayedTeleport(TeleportCancelException cancelException);

    boolean isAsyncLoadingChunks();

    void setAsyncLoadingChunks(boolean loadingChunks);

}
