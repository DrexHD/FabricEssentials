package me.drex.essentials.command.impl.warp;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.storage.DataStorage;
import me.drex.essentials.storage.ServerData;
import me.drex.essentials.util.teleportation.Location;
import me.drex.essentials.util.teleportation.Warp;

import java.util.Map;

import static me.drex.essentials.util.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;

public class SetWarpCommand extends Command {

    private static final DynamicCommandExceptionType ALREADY_EXISTS = new DynamicCommandExceptionType(src -> localized("fabric-essentials.commands.setwarp.already_exists", (CommandSourceStack) src));

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
        ServerPlayer player = src.getPlayerOrException();
        ServerData essentialsData = DataStorage.serverData();
        Map<String, Warp> warps = essentialsData.getWarps();
        if (!warps.containsKey(name)) {
            Warp warp = new Warp(new Location(src));
            warps.put(name, warp);
            src.sendSuccess(() -> localized("fabric-essentials.commands.setwarp", warp.placeholders(name), src), false);
            return SUCCESS;
        } else {
            throw ALREADY_EXISTS.create(src);
        }
    }

}
