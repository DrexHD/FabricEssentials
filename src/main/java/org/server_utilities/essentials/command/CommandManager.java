package org.server_utilities.essentials.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.server_utilities.essentials.command.impl.home.DeleteHomeCommand;
import org.server_utilities.essentials.command.impl.home.HomeCommand;
import org.server_utilities.essentials.command.impl.home.HomesCommand;
import org.server_utilities.essentials.command.impl.home.SetHomeCommand;
import org.server_utilities.essentials.command.impl.menu.*;
import org.server_utilities.essentials.command.impl.misc.*;
import org.server_utilities.essentials.command.impl.misc.admin.*;
import org.server_utilities.essentials.command.impl.teleportation.BackCommand;
import org.server_utilities.essentials.command.impl.tpa.TpAcceptCommand;
import org.server_utilities.essentials.command.impl.tpa.TpAllCommand;
import org.server_utilities.essentials.command.impl.tpa.TpDenyCommand;
import org.server_utilities.essentials.command.impl.tpa.TpaCommand;
import org.server_utilities.essentials.command.impl.warp.DeleteWarpCommand;
import org.server_utilities.essentials.command.impl.warp.SetWarpCommand;
import org.server_utilities.essentials.command.impl.warp.WarpCommand;
import org.server_utilities.essentials.command.impl.warp.WarpsCommand;

public class CommandManager {

    public static final Command[] COMMANDS = {
        // Menu
        new AnvilCommand(),
        new CartographyTableCommand(),
        new EnchantmentCommand(),
        new EnderChestCommand(),
        new GrindstoneCommand(),
        new LoomCommand(),
        new SmithingCommand(),
        new StonecutterCommand(),
        new WorkBenchCommand(),
        // Homes
        new DeleteHomeCommand(),
        new HomeCommand(),
        new HomesCommand(),
        new SetHomeCommand(),
        // Warps
        new DeleteWarpCommand(),
        new WarpCommand(),
        new SetWarpCommand(),
        new WarpsCommand(),
        // Teleportation
        TpaCommand.TPA,
        TpaCommand.TPA_HERE,
        new TpAllCommand(),
        new TpAcceptCommand(),
        new TpDenyCommand(),
        // Util
        new BackCommand(),
        new BroadcastCommand(),
        new CommandSpyCommand(),
        new EssentialsCommand(),
        new FeedCommand(),
        new HatCommand(),
        new ItemEditCommand(),
        new HealCommand(),
        new GlowCommand(),
        new PingCommand(),
        new SignEditCommand(),
        new SignEditCommand(),
        new ModsCommand(),
        new FlyCommand(),
        new FlySpeedCommand(),
        new WalkSpeedCommand(),
        new InvulnerableCommand(),
        new TellMessageCommand(),
    };

    public CommandManager(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        for (Command command : COMMANDS) {
            command.register(dispatcher, context);
        }
    }

}
