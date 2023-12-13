package org.server_utilities.essentials.command.impl.warp;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.ServerData;
import org.server_utilities.essentials.util.teleportation.Location;
import org.server_utilities.essentials.util.teleportation.Warp;

import java.util.Map;

import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;

public class SetWarpCommand extends Command {

    private static final SimpleCommandExceptionType ALREADY_EXISTS = new SimpleCommandExceptionType(localized("fabric-essentials.commands.setwarp.already_exists"));

    public SetWarpCommand() {
        super(CommandProperties.create("setwarp", 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
                argument("warp", StringArgumentType.word())
                        .executes(ctx -> setWarp(ctx.getSource(), StringArgumentType.getString(ctx, "warp")))
        );
    }

    private int setWarp(CommandSourceStack src, String name) throws CommandSyntaxException {
        ServerData essentialsData = DataStorage.serverData();
        Map<String, Warp> warps = essentialsData.getWarps();
        if (!warps.containsKey(name)) {
            Warp warp = new Warp(new Location(src));
            warps.put(name, warp);
            src.sendSuccess(() -> localized("fabric-essentials.commands.setwarp", warp.placeholders(name)), false);
            return SUCCESS;
        } else {
            throw ALREADY_EXISTS.create();
        }
    }

}
