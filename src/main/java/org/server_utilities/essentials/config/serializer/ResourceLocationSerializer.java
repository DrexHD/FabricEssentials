package org.server_utilities.essentials.config.serializer;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.configurate.serialize.ScalarSerializer;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class ResourceLocationSerializer extends ScalarSerializer<ResourceLocation> {

    public ResourceLocationSerializer() {
        super(ResourceLocation.class);
    }

    @Override
    public ResourceLocation deserialize(Type type, Object obj) {
        return new ResourceLocation(obj.toString());
    }

    @Override
    protected Object serialize(ResourceLocation location, Predicate<Class<?>> typeSupported) {
        return location.toString();
    }
}
