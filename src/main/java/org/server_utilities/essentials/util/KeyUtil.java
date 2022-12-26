package org.server_utilities.essentials.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import org.server_utilities.essentials.EssentialsMod;

import java.util.Arrays;
import java.util.stream.Collectors;

public class KeyUtil {

    public static final String PERMISSION_PREFIX = EssentialsMod.MOD_ID;

    public static String join(String... parts) {
        if (parts == null) return "";
        return Arrays.stream(parts).filter(s -> s != null && !s.equals("")).collect(Collectors.joining("."));
    }

    public static String permission(String... nodes) {
        return join(PERMISSION_PREFIX, join(nodes));
    }

    public static boolean permission(CommandSourceStack src, String... nodes) {
        try {
            return Permissions.check(src, permission(nodes), 2);
        } catch (Throwable ignored) {
            // Fallback for datapack compatibility
            return src.hasPermission(2);
        }
    }

    public static boolean permission(Entity entity, String... nodes) {
        try {
            return Permissions.check(entity, permission(nodes), 2);
        } catch (Throwable ignored) {
            // Fallback for datapack compatibility
            return entity.createCommandSourceStack().hasPermission(2);
        }
    }

}
