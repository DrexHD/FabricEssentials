package org.server_utilities.essentials.command.impl.teleportation.home;

import com.mojang.authlib.GameProfile;
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
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOfflineTargetCommand;
import org.server_utilities.essentials.storage.EssentialsDataStorage;
import org.server_utilities.essentials.storage.UserData;
import org.server_utilities.essentials.util.teleportation.Home;

import java.util.Optional;

public class HomeCommand extends OptionalOfflineTargetCommand {

    public static final SimpleCommandExceptionType DOESNT_EXIST = new SimpleCommandExceptionType(Component.translatable("text.fabric-essentials.command.home.doesnt_exist"));
    private static final String NAME = "name";
    public static final String HOME_COMMAND = "home";

    public HomeCommand() {
        super(Properties.create(HOME_COMMAND).permission("home"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        RequiredArgumentBuilder<CommandSourceStack, String> name = Commands.argument(NAME, StringArgumentType.string()).suggests(HOMES_PROVIDER);
        registerOptionalArgument(name);
        literal.then(name);
    }

    @Override
    protected int onSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return teleportHome(ctx, StringArgumentType.getString(ctx, NAME), ctx.getSource().getPlayerOrException().getGameProfile(), true);
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, GameProfile target) throws CommandSyntaxException {
        return teleportHome(ctx, StringArgumentType.getString(ctx, NAME), target, false);
    }

    private int teleportHome(CommandContext<CommandSourceStack> ctx, String name, GameProfile target, boolean self) throws CommandSyntaxException {
        ServerPlayer serverPlayer = ctx.getSource().getPlayerOrException();
        EssentialsDataStorage dataStorage = getEssentialsDataStorage(ctx);
        UserData userData = dataStorage.getUserData(target.getId());
        Optional<Home> optional = userData.getHome(name);
        Home home = optional.orElseThrow(DOESNT_EXIST::create);
        sendFeedback(ctx, String.format("text.fabric-essentials.command.home.teleport.%s", self ? "self" : "other"), name);
        home.getLocation().teleport(serverPlayer);
        return 1;
    }

    public static final SuggestionProvider<CommandSourceStack> HOMES_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.suggest(getEssentialsDataStorage(ctx).getUserData(ctx.getSource().getPlayerOrException().getUUID()).getHomes().stream().map(Home::getName).toList(), builder);

}
