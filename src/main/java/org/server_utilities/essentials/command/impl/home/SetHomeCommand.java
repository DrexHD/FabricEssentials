package org.server_utilities.essentials.command.impl.home;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.config.homes.HomesConfig;
import org.server_utilities.essentials.config.homes.HomesLimit;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;
import org.server_utilities.essentials.util.teleportation.Home;
import org.server_utilities.essentials.util.teleportation.Location;

import java.util.Map;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.arguments.GameProfileArgument.gameProfile;
import static org.server_utilities.essentials.command.impl.home.HomeCommand.DEFAULT_HOME_NAME;
import static org.server_utilities.essentials.command.util.CommandUtil.PROFILES_PROVIDER;
import static org.server_utilities.essentials.command.util.CommandUtil.getGameProfile;

public class SetHomeCommand extends Command {

    private static final SimpleCommandExceptionType ALREADY_EXISTS = new SimpleCommandExceptionType(localized("fabric-essentials.commands.sethome.already_exists"));

    public SetHomeCommand() {
        super(CommandProperties.create("sethome", 0));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal
                .then(
                        argument("home", word())
                                .then(argument("player", gameProfile()).suggests(PROFILES_PROVIDER)
                                        .requires(require("other"))
                                        .executes(ctx -> setHome(ctx.getSource(), getString(ctx, "home"), getGameProfile(ctx, "player"), false))
                                )
                                .executes(ctx -> setHome(ctx.getSource(), getString(ctx, "home"), ctx.getSource().getPlayerOrException().getGameProfile(), true))
                )
                .executes(ctx -> setHome(ctx.getSource(), DEFAULT_HOME_NAME, ctx.getSource().getPlayerOrException().getGameProfile(), true));
    }

    protected int setHome(CommandSourceStack src, String name, GameProfile target, boolean self) throws CommandSyntaxException {
        PlayerData playerData = DataStorage.STORAGE.getOfflinePlayerData(src.getServer(), target.getId());
        Map<String, Home> homes = playerData.getHomes();
        if (!homes.containsKey(name)) {
            int limit = getHomesLimit(src);
            if (homes.size() >= limit) {
                src.sendFailure(localized("fabric-essentials.commands.sethome.limit"));
                return FAILURE;
            } else {
                Home home = new Home(new Location(src));
                homes.put(name, home);
                DataStorage.STORAGE.saveOfflinePlayerData(src.getServer(), target.getId(), playerData);
                if (self) {
                    src.sendSystemMessage(localized("fabric-essentials.commands.sethome.self", home.placeholders(name)));
                } else {
                    src.sendSystemMessage(localized("fabric-essentials.commands.sethome.other", home.placeholders(name), PlaceholderContext.of(target, src.getServer())));
                }
                return SUCCESS;
            }
        } else {
            throw ALREADY_EXISTS.create();
        }
    }

    private int getHomesLimit(CommandSourceStack src) {
        if (check(src, "limit.bypass")) {
            return Integer.MAX_VALUE;
        } else {
            HomesConfig homesConfig = config().homes;
            int limit = homesConfig.defaultLimit;
            int added = 0;
            for (HomesLimit homesLimit : homesConfig.homesLimits) {
                if (check(src, "limit." + homesLimit.permission)) {
                    if (homesLimit.stacks) {
                        added += homesLimit.limit;
                    } else {
                        limit = Math.max(limit, homesLimit.limit);
                    }
                }
            }
            limit += added;
            return limit;
        }
    }

}
