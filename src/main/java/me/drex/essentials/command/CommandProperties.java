package me.drex.essentials.command;

public class CommandProperties {

    private static final String[] EMPTY = new String[]{};

    private final String literal;
    private final String[] alias;
    private final int defaultRequiredLevel;

    private CommandProperties(String literal, String[] alias, int defaultRequiredLevel) {
        this.literal = literal;
        this.alias = alias;
        this.defaultRequiredLevel = defaultRequiredLevel;
    }

    public static CommandProperties create(String literal, int defaultRequiredLevel) {
        return create(literal, EMPTY, defaultRequiredLevel);
    }

    public static CommandProperties create(String literal, String[] alias, int defaultRequiredLevel) {
        return new CommandProperties(literal, alias, defaultRequiredLevel);
    }

    public String[] alias() {
        return alias;
    }

    public String literal() {
        return literal;
    }

    public int defaultRequiredLevel() {
        return defaultRequiredLevel;
    }
}
