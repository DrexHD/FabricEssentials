package org.server_utilities.essentials.command.impl.teleportation;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
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
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;
import org.server_utilities.essentials.util.AsyncChunkLoadUtil;
import org.server_utilities.essentials.util.TeleportationUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
                Commands.literal("check").requires(permission("rtp", "check"))
                        .executes(this::check)
        ).then(
                Commands.literal("add").requires(permission("rtp", "add"))
                        .then(Commands.argument("targets", GameProfileArgument.gameProfile())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes(this::add))
                        )
        );
        literal.executes(this::rtp);
    }

    private int check(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = ctx.getSource().getPlayerOrException();
        PlayerData playerData = DataStorage.STORAGE.getPlayerData(ctx.getSource().getServer(), target.getUUID());
        sendFeedback(ctx, "text.fabric-essentials.command.rtp.check", playerData.getRtpsLeft());
        return playerData.getRtpsLeft();
    }

    // TODO: add messages, rework optional targets
    private int add(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        Collection<GameProfile> targets = GameProfileArgument.getGameProfiles(ctx, "targets");
        for (GameProfile target : targets) {
            PlayerData playerData = DataStorage.STORAGE.getPlayerData(ctx.getSource().getServer(), target.getId());
            playerData.setRtpsLeft(playerData.getRtpsLeft() + amount);
            DataStorage.STORAGE.savePlayerData(ctx.getSource().getServer(), target.getId(), playerData);
        }
        return targets.size();
    }

    private int rtp(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = ctx.getSource().getPlayerOrException();
        ServerLevel targetLevel = ctx.getSource().getLevel();
        PlayerData playerData = DataStorage.STORAGE.getPlayerData(ctx.getSource().getServer(), target.getUUID());
        if (!Permissions.check(ctx.getSource(), createPermission("rtp", "dimension", targetLevel.dimension().location().toString()))) {
            sendError(ctx, "text.fabric-essentials.command.rtp.dimension");
            return -1;
        }
        if (playerData.getRtpsLeft() <= 0 && !Permissions.check(ctx.getSource(), createPermission("rtp", "bypassLimit"))) {
            sendError(ctx, "text.fabric-essentials.command.rtp.no_left");
            return -2;
        }
        if (activeRtps.contains(target.getUUID())) {
            sendError(ctx, "text.fabric-essentials.command.rtp.still_active");
            return -3;
        }
        RTPLocation location = generateLocation(targetLevel);
        if (location == null) {
            sendError(ctx, "text.fabric-essentials.command.rtp.no_location");
            return -4;
        }
        ctx.getSource().getPlayerOrException().displayClientMessage(Component.translatable("text.fabric-essentials.command.async.loading_chunks"), true);
        long start = System.currentTimeMillis();
        activeRtps.add(target.getUUID());
        ChunkPos chunkPos = new ChunkPos(location.x >> 4, location.z >> 4);
        AsyncChunkLoadUtil.scheduleChunkLoadForCommand(ctx.getSource(), targetLevel, chunkPos, throwable -> {
            activeRtps.remove(target.getUUID());
        }).whenCompleteAsync((chunkAccess, throwable) -> {
            execute(ctx.getSource(), target, targetLevel, start, chunkPos, chunkAccess);
        }, ctx.getSource().getServer());
        return 1;
    }

    @Nullable
    private static RTPLocation generateLocation(ServerLevel level) {
        for (int i = 0; i < 50; i++) {
            RTPLocation rtpLocation = getConfig().rtpConfig.shape.generateLocation(getConfig().rtpConfig.centerX, getConfig().rtpConfig.centerZ, getConfig().rtpConfig.minRadius, getConfig().rtpConfig.maxRadius);
            Holder<Biome> holder = level.getBiome(new BlockPos(rtpLocation.x, 70, rtpLocation.z));
            ResourceLocation location = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(holder.value());
            boolean invalid = false;
            for (ResourceLocation blacklisted : getConfig().rtpConfig.blacklistedBiomes) {
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
                sendFeedback(src, "text.fabric-essentials.command.rtp.success", (System.currentTimeMillis() - start));
                if (!Permissions.check(src, createPermission("rtp", "bypassLimit"))) {
                    PlayerData playerData = DataStorage.STORAGE.getPlayerData(src.getServer(), target.getUUID());
                    playerData.setRtpsLeft(playerData.getRtpsLeft() - 1);
                    DataStorage.STORAGE.savePlayerData(src.getServer(), target.getUUID(), playerData);
                }
                activeRtps.remove(target.getUUID());
                return;
            }
        }
        activeRtps.remove(target.getUUID());
        sendError(src, "text.fabric-essentials.command.rtp.unsafe");
    }

}
