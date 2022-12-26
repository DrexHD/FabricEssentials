package org.server_utilities.essentials.command.impl.teleportation.warp;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.drex.message.api.Message;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.ServerData;
import org.server_utilities.essentials.util.teleportation.Warp;

import java.util.Map;

import static org.server_utilities.essentials.command.impl.teleportation.warp.WarpCommand.UNKNOWN;
import static org.server_utilities.essentials.command.impl.teleportation.warp.WarpCommand.WARPS_PROVIDER;

public class DeleteWarpCommand extends Command {

    public DeleteWarpCommand() {
        super(Properties.create("deletewarp", "delwarp"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        RequiredArgumentBuilder<CommandSourceStack, String> name = Commands.argument("name", StringArgumentType.string()).suggests(WARPS_PROVIDER);
        name.executes(this::execute);
        literal.then(name);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String name = StringArgumentType.getString(ctx, "name");
        ServerData essentialsData = DataStorage.STORAGE.getServerData();
        Map<String, Warp> warps = essentialsData.getWarps();
        if (!warps.containsKey(name)) {
            throw UNKNOWN.create();
        } else {
            Warp warp = warps.get(name);
            warps.remove(name);
            ctx.getSource().sendSuccess(Message.message("fabric-essentials.commands.deletewarp", warp.placeholders(name)), false);
        }
        return SUCCESS;
    }

}
