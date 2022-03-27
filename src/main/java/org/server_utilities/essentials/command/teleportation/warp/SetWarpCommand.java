package org.server_utilities.essentials.command.teleportation.warp;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;

// TODO:
public class SetWarpCommand extends Command {

    public SetWarpCommand() {
        super(Properties.create("setwarp").permission("setwarp"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {

    }
}
