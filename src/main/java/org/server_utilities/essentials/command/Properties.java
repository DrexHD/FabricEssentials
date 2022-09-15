package org.server_utilities.essentials.command;

import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class Properties {

    private final String literal;
    private final String[] alias;
    @Nullable
    private String permission;
    @Deprecated
    private Predicate<CommandSourceStack> predicate = stack -> true;

    private Properties(String literal, String... alias) {
        this.literal = literal;
        this.alias = alias;
    }

    public static Properties create(String literal, String... alias) {
        return new Properties(literal, alias);
    }

    public Properties permission(String permission) {
        this.permission = permission;
        return this;
    }

    @Deprecated
    public Properties andPredicate(Predicate<CommandSourceStack> predicate) {
        this.predicate = this.predicate.and(predicate);
        return this;
    }

    @Deprecated
    public Properties orPredicate(Predicate<CommandSourceStack> predicate) {
        this.predicate = this.predicate.or(predicate);
        return this;
    }

    public String[] alias() {
        return alias;
    }

    public String literal() {
        return literal;
    }

    public Predicate<CommandSourceStack> predicate() {
        return predicate;
    }

    @Nullable
    public String permission() {
        return permission;
    }

}
