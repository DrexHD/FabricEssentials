package org.server_utilities.essentials.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.pb4.playerdata.api.PlayerDataApi;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.server_utilities.essentials.EssentialsMod;
import org.server_utilities.essentials.storage.adapter.ResourceLocationAdapter;
import org.server_utilities.essentials.storage.adapter.Vec3Adapter;

import java.util.UUID;

public class DataStorage {

    public static final DataStorage STORAGE = new DataStorage();
    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setLenient()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocationAdapter())
            .registerTypeAdapter(Vec3.class, new Vec3Adapter())
            .create();

    private DataStorage() {
    }

    @NotNull
    public EssentialsData getEssentialsData(MinecraftServer server) {
        EssentialsData data = PlayerDataApi.getCustomDataFor(server, Util.NIL_UUID, EssentialsMod.ESSENTIALS_DATA_STORAGE);
        if (data == null) return new EssentialsData();
        return data;
    }

    public void saveEssentialsData(MinecraftServer server, EssentialsData data) {
        PlayerDataApi.setCustomDataFor(server, Util.NIL_UUID, EssentialsMod.ESSENTIALS_DATA_STORAGE, data);
    }

    @NotNull
    public PlayerData getPlayerData(MinecraftServer server, UUID uuid) {
        PlayerData data = PlayerDataApi.getCustomDataFor(server, uuid, EssentialsMod.USER_DATA_STORAGE);
        if (data == null) data = new PlayerData();
        return data;
    }

    public void savePlayerData(MinecraftServer server, UUID uuid, PlayerData data) {
        PlayerDataApi.setCustomDataFor(server, uuid, EssentialsMod.USER_DATA_STORAGE, data);
    }

}
