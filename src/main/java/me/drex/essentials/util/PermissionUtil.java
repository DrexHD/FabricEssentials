package me.drex.essentials.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import me.drex.essentials.EssentialsMod;
//? if > 1.21.10 {
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
//? }

public class PermissionUtil {


    public static String permission(String permission) {
        return EssentialsMod.MOD_ID + "." + permission;
    }

    public static boolean check(CommandSourceStack src, String permission) {
        try {
            return Permissions.check(src, permission(permission), 2);
        } catch (Throwable ignored) {
            // Fallback for datapack compatibility
            //? if > 1.21.10 {
            return src.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS));
            //?} else {
                /*return src.hasPermission(2);
            *///? }
        }
    }

    public static boolean check(ServerPlayer player, String permission) {
        try {
            return Permissions.check(player.createCommandSourceStack(), permission(permission), 2);
        } catch (Throwable ignored) {
            // Fallback for datapack compatibility
            //? if > 1.21.10 {
            return player.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS));
            //?} else {
                /*return player.createCommandSourceStack().hasPermission(2);
            *///? }
        }
    }

}
