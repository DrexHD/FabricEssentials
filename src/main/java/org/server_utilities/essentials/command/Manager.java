package org.server_utilities.essentials.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.command.menu.AnvilCommand;
import org.server_utilities.essentials.command.menu.EnderChestCommand;
import org.server_utilities.essentials.command.menu.InventoryCommand;
import org.server_utilities.essentials.command.menu.WorkBenchCommand;
import org.server_utilities.essentials.command.teleportation.home.HomeCommand;
import org.server_utilities.essentials.command.teleportation.home.HomesCommand;
import org.server_utilities.essentials.command.teleportation.home.SetHomeCommand;
import org.server_utilities.essentials.command.teleportation.warp.SetWarpCommand;
import org.server_utilities.essentials.command.teleportation.warp.WarpCommand;
import org.server_utilities.essentials.command.teleportation.warp.WarpsCommand;
import org.server_utilities.essentials.command.util.FeedCommand;
import org.server_utilities.essentials.command.util.HealCommand;
import org.server_utilities.essentials.command.util.ModsCommand;
import org.server_utilities.essentials.command.util.PingCommand;

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
            new HomeCommand(),
            new SetHomeCommand(),
            new HomesCommand(),
            // Warps
            new WarpCommand(),
            new SetWarpCommand(),
            new WarpsCommand(),
            // Util
            new PingCommand(),
            new ModsCommand(),
    };

    public Manager(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
        for (Command command : COMMANDS) {
            command.registerCommand(dispatcher);
        }
    }

}
