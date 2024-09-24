package me.drex.essentials.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import me.drex.essentials.EssentialsMod;

public class IdentifierUtil {


    public static String permission(String permission) {
        return EssentialsMod.MOD_ID + "." + permission;
    }

    public static boolean check(CommandSourceStack src, String permission) {
        try {
            return Permissions.check(src, permission(permission), 2);
        } catch (Throwable ignored) {
            // Fallback for datapack compatibility
            return src.hasPermission(2);
        }
    }

    public static boolean check(Entity entity, String permission) {
        try {
            return Permissions.check(entity, permission(permission), 2);
        } catch (Throwable ignored) {
            // Fallback for datapack compatibility
            return entity.createCommandSourceStack().hasPermission(2);
        }
    }

}
