package org.server_utilities.essentials.command;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Predicate;

public class Properties {

    private String[] literals;
    private Predicate<CommandSourceStack> predicate = stack -> true;
    private int minOpLevel = 0;
    private static final String PERMISSION_PREFIX = "fabric_essentials";

    private Properties(String... literals) {
        this.literals = literals;
    }

    public static Properties create(String... literals) {
        return new Properties(literals);
    }

    public Properties permission(String permission) {
        this.predicate = this.predicate.and(commandSourceStack -> Permissions.check(commandSourceStack, String.join(".", PERMISSION_PREFIX, permission), false));
        return this;
    }

    public Properties permission(String permission, int minOpLevel) {
        this.predicate = this.predicate.and(commandSourceStack -> Permissions.check(commandSourceStack, String.join(".", PERMISSION_PREFIX, permission), minOpLevel));
        this.minOpLevel = minOpLevel;
        return this;
    }

    public Properties predicate(Predicate<CommandSourceStack> predicate) {
        this.predicate = this.predicate.and(predicate);
        return this;
    }

    public String[] getLiterals() {
        return literals;
    }

}
