package org.server_utilities.essentials.command.util;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;

// TODO:
public class InvulnerableCommand extends Command {

    public InvulnerableCommand() {
        super(Properties.create("invulnerable", "godmode").permission("invulnerable"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {

    }
}
