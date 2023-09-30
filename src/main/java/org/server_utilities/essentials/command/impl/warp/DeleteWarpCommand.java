package org.server_utilities.essentials.command.impl.warp;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.ServerData;
import org.server_utilities.essentials.util.teleportation.Warp;

import java.util.Map;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;
import static org.server_utilities.essentials.command.impl.warp.WarpCommand.UNKNOWN;
import static org.server_utilities.essentials.command.impl.warp.WarpCommand.WARPS_PROVIDER;

public class DeleteWarpCommand extends Command {

    public DeleteWarpCommand() {
        super(CommandProperties.create("deletewarp", new String[]{"delwarp"}, 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
                argument("warp", string()).suggests(WARPS_PROVIDER)
                        .executes(this::execute)
        );
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String name = getString(ctx, "warp");
        ServerData essentialsData = DataStorage.STORAGE.getServerData();
        Map<String, Warp> warps = essentialsData.getWarps();
        if (!warps.containsKey(name)) {
            throw UNKNOWN.create();
        } else {
            Warp warp = warps.get(name);
            warps.remove(name);
            ctx.getSource().sendSuccess(() -> localized("fabric-essentials.commands.deletewarp", warp.placeholders(name)), false);
        }
        return SUCCESS;
    }

}
