package me.drex.essentials.command.impl.warp;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.storage.DataStorage;
import me.drex.essentials.storage.ServerData;
import me.drex.essentials.util.teleportation.Warp;

import java.util.Map;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;
import static me.drex.essentials.command.impl.warp.WarpCommand.UNKNOWN;
import static me.drex.essentials.command.impl.warp.WarpCommand.WARPS_PROVIDER;

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
        ServerData essentialsData = DataStorage.serverData();
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
