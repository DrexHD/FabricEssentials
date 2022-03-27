package org.server_utilities.essentials.command.util;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;

// TODO:
public class FlyCommand extends Command {

    public FlyCommand() {
        super(Properties.create("fly").permission("fly"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {

    }
}
