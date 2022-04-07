package org.server_utilities.essentials.command;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import static org.server_utilities.essentials.command.Command.PERMISSION_DELIMITER;
import static org.server_utilities.essentials.command.Command.PERMISSION_PREFIX;

public class Properties {

    private final String[] literals;
    @Nullable
    private String permission;
    private Predicate<CommandSourceStack> predicate = stack -> true;

    private Properties(String... literals) {
        this.literals = literals;
    }

    public static Properties create(String... literals) {
        return new Properties(literals);
    }

    public Properties permission(String permission) {
        this.predicate = this.predicate.and(commandSourceStack -> Permissions.check(commandSourceStack, String.join(PERMISSION_DELIMITER, PERMISSION_PREFIX, permission), false));
        this.permission = permission;
        return this;
    }

    public Properties permission(String permission, int minOpLevel) {
        this.predicate = this.predicate.and(commandSourceStack -> Permissions.check(commandSourceStack, String.join(PERMISSION_DELIMITER, PERMISSION_PREFIX, permission), minOpLevel));
        this.permission = permission;
        return this;
    }

    public Properties andPredicate(Predicate<CommandSourceStack> predicate) {
        this.predicate = this.predicate.and(predicate);
        return this;
    }

    public Properties orPredicate(Predicate<CommandSourceStack> predicate) {
        this.predicate = this.predicate.or(predicate);
        return this;
    }

    public String[] getLiterals() {
        return literals;
    }

    public Predicate<CommandSourceStack> getPredicate() {
        return predicate;
    }

    @Nullable
    public String getPermission() {
        return permission;
    }

}
