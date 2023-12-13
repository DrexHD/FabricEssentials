package org.server_utilities.essentials.storage;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.JsonDataStorage;
import eu.pb4.playerdata.impl.BaseGson;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.server_utilities.essentials.EssentialsMod;
import org.server_utilities.essentials.storage.adapter.Vec3Adapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;

public class DataStorage {

    public static final Gson GSON = BaseGson.createBuilder()
        .registerTypeAdapter(Vec3.class, new Vec3Adapter()) // legacy file structure
        .create();
    public static final File FABRIC_ESSENTIALS_DATA = new File("fabric-essentials.json");

    public static final JsonDataStorage<PlayerData> USER_DATA_STORAGE = new JsonDataStorage<>("fabric-essentials", PlayerData.class, GSON);
    private static ServerData serverData = new ServerData();

    private DataStorage() {
    }

    @ApiStatus.Internal
    public static void init() {
        PlayerDataApi.register(USER_DATA_STORAGE);
        ServerLifecycleEvents.SERVER_STARTING.register(DataStorage::load);
    }

    @NotNull
    public static ServerData serverData() {
        return serverData;
    }

    private static void load(MinecraftServer server) {
        if (FABRIC_ESSENTIALS_DATA.exists()) {
            try {
                String json = Files.readString(FABRIC_ESSENTIALS_DATA.toPath());
                ServerData essentialsData = GSON.fromJson(json, ServerData.class);
                serverData = Objects.requireNonNullElseGet(essentialsData, ServerData::new);
            } catch (IOException e) {
                EssentialsMod.LOGGER.error("Couldn't load server data", e);
            }
        }
    }

    @ApiStatus.Internal
    public static void save() {
        try {
            Files.writeString(FABRIC_ESSENTIALS_DATA.toPath(), GSON.toJson(serverData));
        } catch (IOException e) {
            EssentialsMod.LOGGER.error("Couldn't save server data", e);
        }
    }

    @NotNull
    public static PlayerData getPlayerData(ServerPlayer player) {
        PlayerData playerData = PlayerDataApi.getCustomDataFor(player, USER_DATA_STORAGE);
        if (playerData == null) playerData = new PlayerData();
        return playerData;
    }

    @NotNull
    public static PlayerData getAndSavePlayerData(ServerPlayer player) {
        PlayerData playerData = PlayerDataApi.getCustomDataFor(player, USER_DATA_STORAGE);
        if (playerData == null) {
            playerData = new PlayerData();
            PlayerDataApi.setCustomDataFor(player, USER_DATA_STORAGE, playerData);
        }
        return playerData;
    }

    @NotNull
    public static PlayerData getPlayerData(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return getOfflinePlayerData(ctx.getSource().getServer(), ctx.getSource().getPlayerOrException().getUUID());
    }

    @NotNull
    public static PlayerData getOfflinePlayerData(CommandContext<CommandSourceStack> ctx, GameProfile target) {
        return getOfflinePlayerData(ctx.getSource().getServer(), target.getId());
    }

    @NotNull
    public static PlayerData getOfflinePlayerData(MinecraftServer server, UUID uuid) {
        PlayerData playerData = PlayerDataApi.getCustomDataFor(server, uuid, USER_DATA_STORAGE);
        if (playerData == null) {
            playerData = new PlayerData();
        }
        return playerData;
    }

    public static void saveOfflinePlayerData(CommandContext<CommandSourceStack> ctx, GameProfile target, PlayerData playerData) {
        saveOfflinePlayerData(ctx.getSource().getServer(), target.getId(), playerData);
    }

    public static void saveOfflinePlayerData(MinecraftServer server, UUID uuid, PlayerData playerData) {
        PlayerDataApi.setCustomDataFor(server, uuid, USER_DATA_STORAGE, playerData);
    }

}
