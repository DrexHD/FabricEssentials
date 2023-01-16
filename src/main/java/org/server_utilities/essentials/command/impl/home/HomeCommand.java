package org.server_utilities.essentials.command.impl.home;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.drex.message.api.Message;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.command.util.CommandUtil;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;
import org.server_utilities.essentials.util.teleportation.Home;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.arguments.GameProfileArgument.gameProfile;
import static org.server_utilities.essentials.command.util.CommandUtil.PROFILES_PROVIDER;
import static org.server_utilities.essentials.command.util.CommandUtil.getGameProfile;

public class HomeCommand extends Command {

    public static final SimpleCommandExceptionType UNKNOWN = new SimpleCommandExceptionType(Message.message("fabric-essentials.commands.home.unknown"));
    public static final String DEFAULT_HOME_NAME = "home";

    public HomeCommand() {
        super(CommandProperties.create("home", 0));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal
                .then(
                        argument("home", word()).suggests(HOMES_PROVIDER)
                                .then(argument("player", gameProfile()).suggests(PROFILES_PROVIDER)
                                        .requires(require("other"))
                                        .executes(ctx -> teleportHome(ctx.getSource(), getString(ctx, "home"), getGameProfile(ctx, "player"), false))
                                )
                                .executes(ctx -> teleportHome(ctx.getSource(), getString(ctx, "home"), ctx.getSource().getPlayerOrException().getGameProfile(), true))
                )
                .executes(ctx -> teleportHome(ctx.getSource(), DEFAULT_HOME_NAME, ctx.getSource().getPlayerOrException().getGameProfile(), true));
    }

    protected int teleportHome(CommandSourceStack src, String name, GameProfile target, boolean self) throws CommandSyntaxException {
        ServerPlayer serverPlayer = src.getPlayerOrException();
        DataStorage dataStorage = DataStorage.STORAGE;
        PlayerData playerData = dataStorage.getOfflinePlayerData(src.getServer(), target.getId());
        Home home = playerData.getHomes().get(name);
        if (home == null) throw UNKNOWN.create();
        ServerLevel targetLevel = home.location().getLevel(src.getServer());
        if (targetLevel != null) {
            CommandUtil.asyncTeleport(src, targetLevel, home.location().chunkPos(), config().homes.waitingPeriod).whenCompleteAsync((chunkAccess, throwable) -> {
                if (chunkAccess == null) return;
                if (self) {
                    src.sendSystemMessage(Message.message("fabric-essentials.commands.home.self", home.placeholders(name)));
                } else {
                    src.sendSystemMessage(Message.message("fabric-essentials.commands.home.other", home.placeholders(name), PlaceholderContext.of(target, src.getServer())));
                }
                home.location().teleport(serverPlayer);
            }, src.getServer());
            return SUCCESS;
        } else {
            throw WORLD_UNKNOWN.create();
        }
    }

    public static final SuggestionProvider<CommandSourceStack> HOMES_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.suggest(DataStorage.STORAGE.getPlayerData(ctx).getHomes().keySet(), builder);

    public static final SuggestionProvider<CommandSourceStack> OTHER_HOMES_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.suggest(DataStorage.STORAGE.getOfflinePlayerData(ctx.getSource().getServer(), getGameProfile(ctx, "player").getId()).getHomes().keySet(), builder);

}
