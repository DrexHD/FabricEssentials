package me.drex.essentials.command.impl.warp;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.command.util.CommandUtil;
import me.drex.essentials.storage.DataStorage;
import me.drex.essentials.util.teleportation.Warp;

import java.util.Map;

import static me.drex.message.api.LocalizedMessage.localized;

public class WarpCommand extends Command {

    public static final SimpleCommandExceptionType UNKNOWN = new SimpleCommandExceptionType(localized("fabric-essentials.commands.warp.unknown"));

    public WarpCommand() {
        super(CommandProperties.create("warp", 0));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        RequiredArgumentBuilder<CommandSourceStack, String> name = Commands.argument("warp", StringArgumentType.string()).suggests(WARPS_PROVIDER);
        name.executes(this::teleport);
        literal.then(name);
    }

    private int teleport(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "warp");
        ServerPlayer serverPlayer = src.getPlayerOrException();
        Map<String, Warp> warps = DataStorage.serverData().getWarps();
        Warp warp = warps.get(name);
        if (warp == null) throw UNKNOWN.create();
        ServerLevel targetLevel = warp.location().getLevel(src.getServer());
        if (targetLevel != null) {
            CommandUtil.asyncTeleport(src, targetLevel, warp.location().chunkPos(), config().teleportation.waitingPeriod).whenCompleteAsync((chunkAccess, throwable) -> {
                if (chunkAccess == null) return;
                ctx.getSource().sendSuccess(() -> localized("fabric-essentials.commands.warp", warp.placeholders(name)), false);
                warp.location().teleport(serverPlayer);
            }, src.getServer());
        } else {
            throw WORLD_UNKNOWN.create();
        }
        return SUCCESS;
    }

    public static final SuggestionProvider<CommandSourceStack> WARPS_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.suggest(DataStorage.serverData().getWarps().keySet(), builder);

}
