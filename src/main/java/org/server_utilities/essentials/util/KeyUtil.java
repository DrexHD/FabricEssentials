package org.server_utilities.essentials.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.EssentialsMod;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KeyUtil {

    public static final String PERMISSION_PREFIX = EssentialsMod.MOD_ID;
    public static final String TRANSLATION_KEY_PREFIX = join("text", EssentialsMod.MOD_ID);

    public static String join(String... parts) {
        return Arrays.stream(parts).filter(s -> s != null && !s.equals("")).collect(Collectors.joining("."));
    }

    public static String permission(String... nodes) {
        return join(PERMISSION_PREFIX, join(nodes));
    }

    public static Predicate<CommandSourceStack> predicate(String... nodes) {
        return src -> Permissions.check(src, permission(nodes), 2);
    }

    public static String translation(String... nodes) {
        return join(TRANSLATION_KEY_PREFIX, join(nodes));
    }

}
