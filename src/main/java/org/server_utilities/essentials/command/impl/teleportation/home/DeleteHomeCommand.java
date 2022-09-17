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

import static org.server_utilities.essentials.command.impl.teleportation.home.HomeCommand.UNKNOWN;

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
    protected int execute(CommandContext<CommandSourceStack> ctx, GameProfile target, boolean self) throws CommandSyntaxException {
        String name = StringArgumentType.getString(ctx, NAME);
        DataStorage dataStorage = DataStorage.STORAGE;
        PlayerData playerData = dataStorage.getPlayerData(ctx.getSource().getServer(), target.getId());
        Optional<Home> optional = playerData.getHome(name);
        if (optional.isPresent()) {
            playerData.getHomes().remove(optional.get());
            sendQueryFeedbackWithOptionalTarget(ctx, self, new Object[]{name}, new Object[]{name, target.getName()});
            return SUCCESS;
        } else {
            throw UNKNOWN.create();
        }
    }


    public static final SuggestionProvider<CommandSourceStack> HOMES_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.suggest(DataStorage.STORAGE.getPlayerData(ctx.getSource().getServer(), ctx.getSource().getPlayerOrException().getUUID()).getHomes().stream().map(Home::name).toList(), builder);

}
