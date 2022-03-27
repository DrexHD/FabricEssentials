package org.server_utilities.essentials.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public abstract class Command {

    private Properties properties;

    public Command(Properties properties) {
        this.properties = properties;
    }

    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (String literal : this.properties.getLiterals()) {
            LiteralArgumentBuilder<CommandSourceStack> literalArgument = Commands.literal(literal);
            register(literalArgument);
            dispatcher.register(literalArgument);
        }
    }

    protected abstract void register(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder);

    public static final class Builder {

        private String[] literals;

        public Builder(String literal) {

        }

    }

}
