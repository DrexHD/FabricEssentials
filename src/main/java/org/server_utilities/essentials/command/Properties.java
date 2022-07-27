package org.server_utilities.essentials.command;

import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import static org.server_utilities.essentials.command.Command.*;

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
        this.predicate = this.predicate.and(Command.permission(permission));
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

    public String[] literals() {
        return literals;
    }

    public Predicate<CommandSourceStack> predicate() {
        return predicate;
    }

    @Nullable
    public String permission() {
        return permission;
    }

}
