package org.server_utilities.essentials.command.teleportation.warp;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;

// TODO:
public class WarpCommand extends Command {

    public WarpCommand() {
        super(Properties.create("warp").permission("warp"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {

    }
}
