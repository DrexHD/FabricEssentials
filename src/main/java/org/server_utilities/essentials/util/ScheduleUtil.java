package org.server_utilities.essentials.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.server_utilities.essentials.config.ConfigManager;
import org.server_utilities.essentials.config.util.WaitingPeriodConfig;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ScheduleUtil {

    public static final ScheduleUtil INSTANCE = new ScheduleUtil();
    private final List<Data> scheduled = new LinkedList<>();
    private final List<Data> queue = new LinkedList<>();

    private ScheduleUtil() {
    }

    public void init() {
        ServerTickEvents.START_SERVER_TICK.register(this::tick);
    }

    public CompletableFuture<Void> schedule(long duration, TimeUnit timeUnit, Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        queue.add(new Data(System.currentTimeMillis() + timeUnit.toMillis(duration), future, runnable));
        return future;
    }

    public CompletableFuture<WaitingPeriodConfig.WaitingResult> scheduleTeleport(CommandSourceStack src, WaitingPeriodConfig config) {
        CompletableFuture<WaitingPeriodConfig.WaitingResult> resultFuture = new CompletableFuture<>();
        scheduleTeleport(src, config.cancellation, config.period, seconds -> src.sendSuccess(Component.translatable("text.fabric-essentials.teleport.wait", seconds), false), resultFuture);
        resultFuture.whenCompleteAsync((waitingResult, throwable) -> {
            if (waitingResult.isCancelled()) {
                src.sendFailure(Component.translatable(waitingResult.getTranslationKeySelf()));
            }
        }, src.getServer());
        return resultFuture;
    }

    public void scheduleTeleport(CommandSourceStack src, WaitingPeriodConfig.CancellationConfig config, long seconds, Consumer<Long> announcer, CompletableFuture<WaitingPeriodConfig.WaitingResult> successFuture) {
        double distance = src.getPosition().distanceTo(src.getPlayer().position());
        if (config.maxMoveDistance >= 0 && distance >= config.maxMoveDistance) {
            successFuture.complete(WaitingPeriodConfig.WaitingResult.MOVE);
            return;
        }
        if (config.damage && src.getPlayer().getLastDamageSource() != null) {
            successFuture.complete(WaitingPeriodConfig.WaitingResult.DAMAGE);
            return;
        }
        if (src.getServer().getPlayerList().getPlayer(src.getPlayer().getUUID()) == null) {
            successFuture.complete(WaitingPeriodConfig.WaitingResult.OFFLINE);
            return;
        }
        if (seconds >= 1) {
            if (ConfigManager.INSTANCE.config().misc.announcedTimeDuration.contains(seconds)) {
                announcer.accept(seconds);
            }
            schedule(1, TimeUnit.SECONDS, () -> scheduleTeleport(src, config, seconds - 1, announcer, successFuture));
        } else {
            successFuture.complete(WaitingPeriodConfig.WaitingResult.SUCCESS);
        }
    }

    private void tick(MinecraftServer server) {
        scheduled.addAll(queue);
        queue.clear();
        Iterator<Data> iterator = scheduled.iterator();
        while (iterator.hasNext()) {
            Data data = iterator.next();
            if (System.currentTimeMillis() >= data.scheduledTimeMillis()) {
                data.runnable().run();
                data.future().complete(null);
                iterator.remove();
            }
        }
    }

    private record Data(long scheduledTimeMillis, CompletableFuture<Void> future, Runnable runnable) {
    }

}
