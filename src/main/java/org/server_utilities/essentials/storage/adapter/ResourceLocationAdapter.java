package org.server_utilities.essentials.storage.adapter;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class ResourceLocationAdapter extends TypeAdapter<ResourceLocation> {

    @Override
    public void write(JsonWriter out, @Nullable ResourceLocation value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString());
        }
    }

    @Override
    public ResourceLocation read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            try {
                return new ResourceLocation(in.nextString());
            } catch (Exception e) {
                throw new JsonSyntaxException(e);
            }
        }
    }
}
