package org.server_utilities.essentials.command.impl.teleportation.warp;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;

// TODO:
public class WarpsCommand extends Command {

    public WarpsCommand() {
        super(Properties.create("warps").permission("warps"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {

    }
}
