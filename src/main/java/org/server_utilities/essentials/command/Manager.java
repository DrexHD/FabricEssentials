package org.server_utilities.essentials.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.command.impl.menu.AnvilCommand;
import org.server_utilities.essentials.command.impl.menu.EnderChestCommand;
import org.server_utilities.essentials.command.impl.menu.InventoryCommand;
import org.server_utilities.essentials.command.impl.menu.WorkBenchCommand;
import org.server_utilities.essentials.command.impl.teleportation.home.*;
import org.server_utilities.essentials.command.impl.teleportation.warp.SetWarpCommand;
import org.server_utilities.essentials.command.impl.teleportation.warp.WarpCommand;
import org.server_utilities.essentials.command.impl.teleportation.warp.WarpsCommand;
import org.server_utilities.essentials.command.impl.util.*;

// TODO:
public class Manager {

    public static final Command[] COMMANDS = {
            // Menu
            new AnvilCommand(),
            new EnderChestCommand(),
            new FeedCommand(),
            new HealCommand(),
            new InventoryCommand(),
            new WorkBenchCommand(),
            // Homes
            new DeleteHomeCommand(),
            new HomeCommand(),
            new HomesCommand(),
            new SetHomeCommand(),
            // Warps
            new WarpCommand(),
            new SetWarpCommand(),
            new WarpsCommand(),
            // Util
            new PingCommand(),
            new ModsCommand(),
            new FlyCommand(),
    };

    public Manager(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
        for (Command command : COMMANDS) {
            command.registerCommand(dispatcher);
        }
    }

}
