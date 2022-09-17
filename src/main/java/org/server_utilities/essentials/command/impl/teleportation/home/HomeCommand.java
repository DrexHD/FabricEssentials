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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOfflineTargetCommand;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;
import org.server_utilities.essentials.util.teleportation.Home;

import java.util.Optional;

public class HomeCommand extends OptionalOfflineTargetCommand {

    public static final SimpleCommandExceptionType UNKNOWN = new SimpleCommandExceptionType(Component.translatable("text.fabric-essentials.command.home.unknown"));
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
    protected int execute(CommandContext<CommandSourceStack> ctx, GameProfile target, boolean self) throws CommandSyntaxException {
        String name = StringArgumentType.getString(ctx, NAME);
        CommandSourceStack src = ctx.getSource();
        ServerPlayer serverPlayer = src.getPlayerOrException();
        DataStorage dataStorage = DataStorage.STORAGE;
        PlayerData playerData = dataStorage.getPlayerData(src.getServer(), target.getId());
        Optional<Home> optional = playerData.getHome(name);
        Home home = optional.orElseThrow(UNKNOWN::create);
        ServerLevel targetLevel = home.location().getLevel(src.getServer());
        if (targetLevel != null) {
            asyncTeleport(src, targetLevel, home.location().getChunkPos(), config().homes.waitingPeriod).whenCompleteAsync((chunkAccessOptional, throwable) -> {
                if (chunkAccessOptional.isPresent()) {
                    sendQueryFeedbackWithOptionalTarget(ctx, self, new Object[]{name}, new Object[]{name, target.getName()});
                    home.location().teleport(serverPlayer);
                }
            }, src.getServer());
            return SUCCESS;
        } else {
            throw WORLD_UNKNOWN.create();
        }
    }

    public static final SuggestionProvider<CommandSourceStack> HOMES_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.suggest(DataStorage.STORAGE.getPlayerData(ctx.getSource().getServer(), ctx.getSource().getPlayerOrException().getUUID()).getHomes().stream().map(Home::name).toList(), builder);

}
