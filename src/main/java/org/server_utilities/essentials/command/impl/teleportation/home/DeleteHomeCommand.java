package org.server_utilities.essentials.command.impl.teleportation.home;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOfflineTargetCommand;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;
import org.server_utilities.essentials.util.teleportation.Home;

import java.util.Optional;

import static org.server_utilities.essentials.command.impl.teleportation.home.HomeCommand.DOESNT_EXIST;

public class DeleteHomeCommand extends OptionalOfflineTargetCommand {

    private static final String NAME = "name";

    public DeleteHomeCommand() {
        super(Properties.create("deletehome", "delhome", "removehome").permission("deletehome"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        RequiredArgumentBuilder<CommandSourceStack, String> name = Commands.argument(NAME, StringArgumentType.string()).suggests(HOMES_PROVIDER);
        registerOptionalArgument(name);
        literal.then(name);
    }

    @Override
    protected int onSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return deleteHome(ctx, StringArgumentType.getString(ctx, NAME), ctx.getSource().getPlayerOrException().getGameProfile(), true);
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, GameProfile target) throws CommandSyntaxException {
        return deleteHome(ctx, StringArgumentType.getString(ctx, NAME), target, false);
    }

    private int deleteHome(CommandContext<CommandSourceStack> ctx, String name, GameProfile target, boolean self) throws CommandSyntaxException {
        DataStorage dataStorage = DataStorage.STORAGE;
        PlayerData playerData = dataStorage.getPlayerData(ctx.getSource().getServer(), target.getId());
        Optional<Home> optional = playerData.getHome(name);
        if (optional.isPresent()) {
            playerData.getHomes().remove(optional.get());
            dataStorage.savePlayerData(ctx.getSource().getServer(), target.getId(), playerData);
            sendFeedback(ctx, String.format("text.fabric-essentials.command.delhome.%s", self ? "self" : "other"), name);
            return 1;
        } else {
            throw DOESNT_EXIST.create();
        }
    }

    public static final SuggestionProvider<CommandSourceStack> HOMES_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.suggest(DataStorage.STORAGE.getPlayerData(ctx.getSource().getServer(), ctx.getSource().getPlayerOrException().getUUID()).getHomes().stream().map(Home::name).toList(), builder);

}
