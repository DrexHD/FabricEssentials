package me.drex.essentials.storage.adapter;

import com.google.gson.*;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Type;

public class Vec3Adapter implements JsonSerializer<Vec3>, JsonDeserializer<Vec3> {

    @Override
    public JsonElement serialize(Vec3 src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("x", src.x);
        jsonObject.addProperty("y", src.y);
        jsonObject.addProperty("z", src.z);
        return jsonObject;
    }

    @Override
    public Vec3 deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        return new Vec3(
                jsonObject.getAsJsonPrimitive("x").getAsDouble(),
                jsonObject.getAsJsonPrimitive("y").getAsDouble(),
                jsonObject.getAsJsonPrimitive("z").getAsDouble()
        );
    }
}
