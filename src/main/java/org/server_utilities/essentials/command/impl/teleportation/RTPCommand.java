package org.server_utilities.essentials.command.impl.teleportation;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.config.util.WaitingPeriodConfig;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;
import org.server_utilities.essentials.util.AsyncChunkLoadUtil;
import org.server_utilities.essentials.util.ScheduleUtil;
import org.server_utilities.essentials.util.TeleportationUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.server_utilities.essentials.EssentialsMod.MOD_ID;

public class RTPCommand extends Command {

    public static final TagKey<Block> UNSAFE_RTP_LOCATION = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(MOD_ID, "unsafe_rtp_location"));

    private final Set<UUID> activeRtps = new HashSet<>();

    public RTPCommand() {
        super(Properties.create("rtp", "wild").permission("rtp"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.then(
                Commands.literal("check").requires(predicate("check"))
                        .executes(this::check)
        ).then(
                Commands.literal("add").requires(predicate("add"))
                        .then(Commands.argument("targets", GameProfileArgument.gameProfile())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes(this::add))
                        )
        );
        literal.executes(this::rtp);
    }

    private int check(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = ctx.getSource().getPlayerOrException();
        PlayerData playerData = DataStorage.STORAGE.getPlayerData(ctx.getSource().getServer(), target.getUUID());
        sendSuccess(ctx.getSource(), "check", playerData.rtpsLeft);
        return playerData.rtpsLeft;
    }

    // TODO: add messages, rework optional targets
    private int add(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        Collection<GameProfile> targets = GameProfileArgument.getGameProfiles(ctx, "targets");
        for (GameProfile target : targets) {
            PlayerData playerData = DataStorage.STORAGE.getPlayerData(ctx.getSource().getServer(), target.getId());
            playerData.rtpsLeft += amount;
            DataStorage.STORAGE.savePlayerData(ctx.getSource().getServer(), target.getId(), playerData);
        }
        return targets.size();
    }

    private int rtp(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        ServerPlayer target = src.getPlayerOrException();
        ServerLevel targetLevel = src.getLevel();
        PlayerData playerData = DataStorage.STORAGE.getPlayerData(src.getServer(), target.getUUID());
        ResourceLocation resourceLocation = targetLevel.dimension().location();
        if (!predicate("dimension", resourceLocation.getNamespace(), resourceLocation.getPath()).test(src)) {
            sendFailure(ctx.getSource(), "dimension");
            return FAILURE;
        }
        if (playerData.rtpsLeft <= 0 && !predicate("bypassLimit").test(src)) {
            sendFailure(ctx.getSource(), "limit");
            return FAILURE;
        }
        if (activeRtps.contains(target.getUUID())) {
            sendFailure(ctx.getSource(), "still_active");
            return FAILURE;
        }
        RTPLocation location = generateLocation(targetLevel);
        if (location == null) {
            sendFailure(ctx.getSource(), "no_location");
            return FAILURE;
        }
        long start = System.currentTimeMillis();
        activeRtps.add(target.getUUID());
        ChunkPos chunkPos = new ChunkPos(location.x >> 4, location.z >> 4);
        CompletableFuture<WaitingPeriodConfig.WaitingResult> waitingPeriodFuture = ScheduleUtil.INSTANCE.scheduleTeleport(src, config().rtp.waitingPeriod);
        CompletableFuture<ChunkAccess> chunkLoadFuture = AsyncChunkLoadUtil.scheduleChunkLoadForCommand(src, targetLevel, chunkPos, throwable -> {
            activeRtps.remove(target.getUUID());
        });
        CompletableFuture.allOf(waitingPeriodFuture,
                chunkLoadFuture
        ).whenCompleteAsync((chunkAccess, throwable) -> {
            if (waitingPeriodFuture.join().isCancelled()) {
                activeRtps.remove(target.getUUID());
                return;
            }
            execute(src, target, targetLevel, start, chunkPos, chunkLoadFuture.join());
        }, src.getServer());
        return SUCCESS;
    }

    @Nullable
    private static RTPLocation generateLocation(ServerLevel level) {
        for (int i = 0; i < 50; i++) {
            RTPLocation rtpLocation = config().rtp.shape.generateLocation(config().rtp.centerX, config().rtp.centerZ, config().rtp.minRadius, config().rtp.maxRadius);
            Holder<Biome> holder = level.getBiome(new BlockPos(rtpLocation.x, 70, rtpLocation.z));
            ResourceLocation location = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(holder.value());
            boolean invalid = false;
            for (ResourceLocation blacklisted : config().rtp.blacklistedBiomes) {
                if (blacklisted.equals(location)) {
                    invalid = true;
                    break;
                }
            }
            if (!invalid) {
                return rtpLocation;
            }
        }
        // No location found
        return null;
    }

    public record RTPLocation(int x, int z) {
    }

    private void execute(CommandSourceStack src, ServerPlayer target, ServerLevel targetLevel, long start, ChunkPos chunkPos, ChunkAccess chunkAccess) {
        if (target.connection == null) {
            activeRtps.remove(target.getUUID());
            return;
        }

        // Search for a safe spot inside the chunk
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = chunkPos.getBlockX(dx);
                int z = chunkPos.getBlockZ(dz);
                int y = chunkAccess.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                BlockPos blockPos = new BlockPos(x, y + 1, z);
                BlockState blockState = chunkAccess.getBlockState(blockPos.below());
                if (blockState.is(UNSAFE_RTP_LOCATION)) {
                    continue;
                }
                LOGGER.debug("Teleporting {} to {} with {}", target.getScoreboardName(), blockPos, Registry.BLOCK.getKey(blockState.getBlock()));
                TeleportationUtil.teleportEntity(target, targetLevel, blockPos);
                sendSuccess(src, join(), (System.currentTimeMillis() - start));
                if (!predicate("bypassLimit").test(src)) {
                    PlayerData playerData = DataStorage.STORAGE.getPlayerData(src.getServer(), target.getUUID());
                    playerData.rtpsLeft--;
                    DataStorage.STORAGE.savePlayerData(src.getServer(), target.getUUID(), playerData);
                }
                activeRtps.remove(target.getUUID());
                return;
            }
        }
        activeRtps.remove(target.getUUID());
        sendFailure(src, "unsafe");
    }

}
