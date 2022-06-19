package org.server_utilities.essentials.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.server_utilities.essentials.command.impl.menu.AnvilCommand;
import org.server_utilities.essentials.command.impl.menu.EnderChestCommand;
import org.server_utilities.essentials.command.impl.menu.InventoryCommand;
import org.server_utilities.essentials.command.impl.menu.WorkBenchCommand;
import org.server_utilities.essentials.command.impl.teleportation.home.DeleteHomeCommand;
import org.server_utilities.essentials.command.impl.teleportation.home.HomeCommand;
import org.server_utilities.essentials.command.impl.teleportation.home.HomesCommand;
import org.server_utilities.essentials.command.impl.teleportation.home.SetHomeCommand;
import org.server_utilities.essentials.command.impl.teleportation.warp.SetWarpCommand;
import org.server_utilities.essentials.command.impl.teleportation.warp.WarpCommand;
import org.server_utilities.essentials.command.impl.teleportation.warp.WarpsCommand;
import org.server_utilities.essentials.command.impl.util.*;

// TODO:
public class CommandManager {

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
            new EssentialsCommand(),
            new PingCommand(),
            new ModsCommand(),
            new FlyCommand(),
            new InvulnerableCommand(),
    };

    public CommandManager(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        for (Command command : COMMANDS) {
            command.registerCommand(dispatcher);
        }
    }

}
