package org.server_utilities.essentials.command.impl.teleportation.warp;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.EssentialsData;
import org.server_utilities.essentials.util.teleportation.Warp;

import java.util.Optional;

public class WarpCommand extends Command {

    public static final SimpleCommandExceptionType UNKNOWN = new SimpleCommandExceptionType(Component.translatable("text.fabric-essentials.command.warp.unknown"));
    private static final String NAME = "name";
    public static final String WARP_COMMAND = "warp";

    public WarpCommand() {
        super(Properties.create(WARP_COMMAND).permission("warp"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        RequiredArgumentBuilder<CommandSourceStack, String> name = Commands.argument(NAME, StringArgumentType.string()).suggests(WARPS_PROVIDER);
        name.executes(this::execute);
        literal.then(name);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        String name = StringArgumentType.getString(ctx, NAME);
        ServerPlayer serverPlayer = src.getPlayerOrException();
        EssentialsData essentialsData = DataStorage.STORAGE.getEssentialsData(src.getServer());
        Optional<Warp> optional = essentialsData.getWarp(name);
        Warp warp = optional.orElseThrow(UNKNOWN::create);
        ServerLevel targetLevel = warp.location().getLevel(src.getServer());
        if (targetLevel != null) {
            asyncTeleport(src, targetLevel, warp.location().getChunkPos(), config().warps.waitingPeriod).whenCompleteAsync((chunkAccessOptional, throwable) -> {
                if (chunkAccessOptional.isPresent()) {
                    sendSuccess(ctx.getSource(), "teleport", name);
                    warp.location().teleport(serverPlayer);
                }
            }, src.getServer());
        } else {
            throw WORLD_UNKNOWN.create();
        }
        return SUCCESS;
    }

    public static final SuggestionProvider<CommandSourceStack> WARPS_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.suggest(DataStorage.STORAGE.getEssentialsData(ctx.getSource().getServer()).getWarps().stream().map(Warp::name).toList(), builder);

}
