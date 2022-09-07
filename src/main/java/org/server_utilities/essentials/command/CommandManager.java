package org.server_utilities.essentials.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.server_utilities.essentials.command.impl.menu.AnvilCommand;
import org.server_utilities.essentials.command.impl.menu.EnderChestCommand;
import org.server_utilities.essentials.command.impl.menu.InventoryCommand;
import org.server_utilities.essentials.command.impl.menu.WorkBenchCommand;
import org.server_utilities.essentials.command.impl.teleportation.RTPCommand;
import org.server_utilities.essentials.command.impl.teleportation.home.DeleteHomeCommand;
import org.server_utilities.essentials.command.impl.teleportation.home.HomeCommand;
import org.server_utilities.essentials.command.impl.teleportation.home.HomesCommand;
import org.server_utilities.essentials.command.impl.teleportation.home.SetHomeCommand;
import org.server_utilities.essentials.command.impl.teleportation.tpa.TpAcceptCommand;
import org.server_utilities.essentials.command.impl.teleportation.tpa.TpaCommand;
import org.server_utilities.essentials.command.impl.teleportation.warp.SetWarpCommand;
import org.server_utilities.essentials.command.impl.teleportation.warp.WarpCommand;
import org.server_utilities.essentials.command.impl.teleportation.warp.WarpsCommand;
import org.server_utilities.essentials.command.impl.util.*;
import org.server_utilities.essentials.util.TprManager;

// TODO:
public class CommandManager {

    public static final Command[] COMMANDS = {
            // Menu
            new AnvilCommand(),
            new EnderChestCommand(),
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
            // Teleportation
            new RTPCommand(),
            new TpaCommand(TprManager.Direction.HERE),
            new TpaCommand(TprManager.Direction.THERE),
            new TpAcceptCommand(),
            // Util
            new EssentialsCommand(),
            new FeedCommand(),
            new HatCommand(),
            new HealCommand(),
            new GlowCommand(),
            new PingCommand(),
            new ModsCommand(),
            new FlyCommand(),
            new InvulnerableCommand(),
            new StaffMessageCommand(),
    };

    public CommandManager(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        for (Command command : COMMANDS) {
            command.registerCommand(dispatcher);
        }
    }

}
