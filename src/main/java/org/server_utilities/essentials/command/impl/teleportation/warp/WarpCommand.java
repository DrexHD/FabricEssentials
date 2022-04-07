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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.storage.EssentialsData;
import org.server_utilities.essentials.util.teleportation.Warp;

import java.util.Optional;

public class WarpCommand extends Command {

    public static final SimpleCommandExceptionType DOESNT_EXIST = new SimpleCommandExceptionType(new TranslatableComponent("text.fabric-essentials.command.warp.doesnt_exist"));
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
        String name = StringArgumentType.getString(ctx, NAME);
        ServerPlayer serverPlayer = ctx.getSource().getPlayerOrException();
        EssentialsData essentialsData = getEssentialsDataStorage(ctx).getEssentialsData();
        Optional<Warp> optional = essentialsData.getWarp(name);
        if (optional.isPresent()) {
            sendFeedback(ctx, "text.fabric-essentials.command.warp.teleport", name);
            optional.get().getLocation().teleport(serverPlayer);
            return 1;
        } else {
            throw DOESNT_EXIST.create();
        }
    }

    public static final SuggestionProvider<CommandSourceStack> WARPS_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.suggest(getEssentialsDataStorage(ctx).getEssentialsData().getWarps().stream().map(Warp::getName).toList(), builder);

}
