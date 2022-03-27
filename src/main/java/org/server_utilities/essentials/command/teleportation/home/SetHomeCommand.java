package org.server_utilities.essentials.command.teleportation.home;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;

// TODO:
public class SetHomeCommand extends Command {

    public SetHomeCommand() {
        super(Properties.create("sethome").permission("sethome"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {

    }
}
