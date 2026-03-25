package me.drex.essentials.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import me.drex.essentials.EssentialsMod;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;

public class PermissionUtil {


    public static String permission(String permission) {
        return EssentialsMod.MOD_ID + "." + permission;
    }

    public static boolean check(CommandSourceStack src, String permission) {
        try {
            return Permissions.check(src, permission(permission), 2);
        } catch (Throwable ignored) {
            return src.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS));
        }
    }

    public static boolean check(ServerPlayer player, String permission) {
        try {
            return Permissions.check(player.createCommandSourceStack(), permission(permission), 2);
        } catch (Throwable ignored) {
            return player.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS));
        }
    }

}
