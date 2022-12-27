package org.server_utilities.essentials.command.impl.teleportation;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.drex.message.api.Message;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.config.rtp.RtpConfig;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;
import org.server_utilities.essentials.util.ComponentPlaceholderUtil;
import org.server_utilities.essentials.util.TeleportationUtil;
import org.server_utilities.essentials.util.teleportation.Location;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import static org.server_utilities.essentials.EssentialsMod.MOD_ID;

public class RTPCommand extends Command {

    public static final TagKey<Block> UNSAFE_RTP_LOCATION = TagKey.create(Registries.BLOCK, new ResourceLocation(MOD_ID, "unsafe_rtp_location"));

    public RTPCommand() {
        super(Properties.create("rtp", "wild"));
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
        PlayerData playerData = DataStorage.STORAGE.getPlayerData(target);
        ctx.getSource().sendSystemMessage(Message.message("fabric-essentials.commands.rtp.check"));
        return playerData.rtpCount;
    }

    // TODO: add messages, rework optional targets
    private int add(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        int amount = IntegerArgumentType.getInteger(ctx, "amount");
        Collection<GameProfile> targets = GameProfileArgument.getGameProfiles(ctx, "targets");
        for (GameProfile target : targets) {
            PlayerData playerData = DataStorage.STORAGE.getOfflinePlayerData(ctx, target);
            playerData.rtpCount += amount;
            DataStorage.STORAGE.saveOfflinePlayerData(ctx, target, playerData);
        }
        return targets.size();
    }

    private int rtp(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        ServerPlayer target = src.getPlayerOrException();
        ServerLevel targetLevel = src.getLevel();
        PlayerData playerData = DataStorage.STORAGE.getPlayerData(target);
        ResourceLocation resourceLocation = targetLevel.dimension().location();
        if (!permission(src, "dimension", resourceLocation.getNamespace(), resourceLocation.getPath())) {
            ctx.getSource().sendFailure(Message.message("fabric-essentials.commands.rtp.dimension"));
            return FAILURE;
        }
        if (playerData.rtpCount <= 0 && !permission(src, "bypassLimit")) {
            ctx.getSource().sendFailure(Message.message("fabric-essentials.commands.rtp.limit"));

            return FAILURE;
        }
        ChunkPos chunkPos = generateLocation(targetLevel);
        if (chunkPos == null) {
            ctx.getSource().sendFailure(Message.message("fabric-essentials.commands.rtp.no_location"));
            return FAILURE;
        }
        long start = System.currentTimeMillis();
        asyncTeleport(src, targetLevel, chunkPos, config().rtp.waitingPeriod).whenCompleteAsync((chunkAccess, throwable) -> {
            if (chunkAccess != null) execute(src, target, targetLevel, start, chunkPos, chunkAccess);
        }, src.getServer());
        return SUCCESS;
    }

    @Nullable
    private ChunkPos generateLocation(ServerLevel level) {
        RtpConfig config = config().rtp;
        for (int i = 0; i < 50; i++) {
            ChunkPos chunkPos = config.shape.generateLocation(config.centerX, config.centerZ, config.minRadius, config.maxRadius);
            Holder<Biome> holder = level.getBiome(chunkPos.getMiddleBlockPosition(70));
            ResourceLocation location = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(holder.value());
            if (!Arrays.stream(config.blacklistedBiomes).toList().contains(location)) {
                return chunkPos;
            }
        }
        // No location found
        return null;
    }

    private void execute(CommandSourceStack src, ServerPlayer target, ServerLevel targetLevel, long start, ChunkPos chunkPos, ChunkAccess chunkAccess) {
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
                LOGGER.debug("Teleporting {} to {} with {}", target.getScoreboardName(), blockPos, BuiltInRegistries.BLOCK.getKey(blockState.getBlock()));
                Location location = new Location(new Vec3(x, y + 1, z), 0, 0, targetLevel.dimension().location());
                src.sendSuccess(Message.message("fabric-essentials.commands.rtp", ComponentPlaceholderUtil.mergePlaceholderMaps(new HashMap<>() {{
                    put("time", Component.literal(String.valueOf(System.currentTimeMillis() - start)));
                }}, location.placeholders())), false);
                TeleportationUtil.teleportEntity(target, targetLevel, blockPos);
                if (!permission(src, "bypassLimit")) {
                    DataStorage.STORAGE.getAndSavePlayerData(target).rtpCount--;
                }
                return;
            }
        }
        src.sendFailure(Message.message("fabric-essentials.commands.rtp.unsafe"));
    }

}
