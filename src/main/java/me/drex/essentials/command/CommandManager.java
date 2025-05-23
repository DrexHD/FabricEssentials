package me.drex.essentials.command;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.tree.CommandNode;
import me.drex.essentials.command.impl.menu.*;
import me.drex.essentials.command.impl.misc.*;
import me.drex.essentials.command.impl.misc.admin.*;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import me.drex.essentials.command.impl.home.DeleteHomeCommand;
import me.drex.essentials.command.impl.home.HomeCommand;
import me.drex.essentials.command.impl.home.HomesCommand;
import me.drex.essentials.command.impl.home.SetHomeCommand;
import me.drex.essentials.command.impl.teleportation.BackCommand;
import me.drex.essentials.command.impl.tpa.TpAcceptCommand;
import me.drex.essentials.command.impl.tpa.TpAllCommand;
import me.drex.essentials.command.impl.tpa.TpDenyCommand;
import me.drex.essentials.command.impl.tpa.TpaCommand;
import me.drex.essentials.command.impl.warp.DeleteWarpCommand;
import me.drex.essentials.command.impl.warp.SetWarpCommand;
import me.drex.essentials.command.impl.warp.WarpCommand;
import me.drex.essentials.command.impl.warp.WarpsCommand;

import java.util.Arrays;
import java.util.Map;

public class CommandManager {

    private static final boolean DUMP_COMMANDS = false;

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
        new ModsCommand(),
        new FlyCommand(),
        new FlySpeedCommand(),
        new WalkSpeedCommand(),
        new InvulnerableCommand(),
        new TellMessageCommand(),
        new MessageToVanilla(),
    };

    public CommandManager(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        for (Command command : COMMANDS) {
            command.register(dispatcher, context);
        }
    }

    public static void dumpCommands(CommandDispatcher<CommandSourceStack> dispatcher, MinecraftServer server) {
        if (!DUMP_COMMANDS) return;
        var source = server.createCommandSourceStack();

        String simple = String.join(", ", Arrays.stream(COMMANDS).map(command -> {
            var properties = command.commandProperties;
            ParseResults<CommandSourceStack> parseResults = dispatcher.parse(properties.literal(), source);
            Map<CommandNode<CommandSourceStack>, String> map = dispatcher.getSmartUsage((Iterables.getLast(parseResults.getContext().getNodes())).getNode(), source);
            if (map.isEmpty()) {
                return "`/" + properties.literal() + "`";
            } else {
                return String.join(
                    ", ",
                    map.values().stream()
                        .map(s -> "`/" + properties.literal() + " " + s.replace("|", "\\|") + "`")
                        .toList()
                );

            }
        }).toList());
        System.out.println(simple);

        StringBuilder builder = new StringBuilder();
        builder
            .append("| Command | Alias | Permission | Default |\n")
            .append("|---|---|---|---|\n");
        for (Command command : COMMANDS) {
            // Command
            var properties = command.commandProperties;
            builder.append("| ");
            ParseResults<CommandSourceStack> parseResults = dispatcher.parse(properties.literal(), source);
            Map<CommandNode<CommandSourceStack>, String> map = dispatcher.getSmartUsage((Iterables.getLast(parseResults.getContext().getNodes())).getNode(), source);
            if (map.isEmpty()) {
                builder.append("`/").append(properties.literal()).append('`');
            } else {
                builder.append(
                    String.join(
                        ", ",
                        map.values().stream()
                            .map(s -> "`/" + properties.literal() + " " + s.replace("|", "\\|") + "`")
                            .toList()
                    )
                );
            }
            builder.append(" | ");
            // Alias
            builder.append(String.join(", ",
                Arrays.stream(properties.alias())
                    .map(s -> "`/" + s + "`")
                    .toList()));
            builder.append(" | ");
            // Permission
            builder.append("`fabric-essentials.command.").append(properties.literal()).append('`');
            builder.append(" | ");
            // Default
            if (properties.defaultRequiredLevel() <= 0) {
                builder.append('✔');
            } else {
                builder.append('✘');
            }
            builder.append(" |\n");
        }
        System.out.println(builder);
    }

}
