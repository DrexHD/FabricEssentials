package org.server_utilities.essentials.command;

import net.minecraft.commands.CommandSourceStack;

import java.util.function.Predicate;

public class Properties {

    private final String literal;
    private final String[] alias;
    @Deprecated
    private Predicate<CommandSourceStack> predicate = stack -> true;

    private Properties(String literal, String... alias) {
        this.literal = literal;
        this.alias = alias;
    }

    public static Properties create(String literal, String... alias) {
        return new Properties(literal, alias);
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

}
