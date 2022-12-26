package org.server_utilities.essentials.command.impl.teleportation.home;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.drex.message.api.Message;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOfflineTargetCommand;
import org.server_utilities.essentials.config.homes.HomesConfig;
import org.server_utilities.essentials.config.homes.HomesLimit;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;
import org.server_utilities.essentials.util.teleportation.Home;
import org.server_utilities.essentials.util.teleportation.Location;

import java.util.Map;

public class SetHomeCommand extends OptionalOfflineTargetCommand {

    private static final SimpleCommandExceptionType ALREADY_EXISTS = new SimpleCommandExceptionType(Message.message("fabric-essentials.commands.sethome.already_exists"));
    private static final String NAME = "name";
    private static final String PERMISSION_LIMIT = "limit";
    private static final String PERMISSION_LIMIT_BYPASS = "bypass";

    public SetHomeCommand() {
        super(Properties.create("sethome"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        RequiredArgumentBuilder<CommandSourceStack, String> name = Commands.argument(NAME, StringArgumentType.string());
        registerOptionalArgument(name);
        literal.then(name).executes(ctx -> execute(ctx, "home", getSelf(ctx), true));
    }

    @Override
    protected int execute(CommandContext<CommandSourceStack> ctx, GameProfile target, boolean self) throws CommandSyntaxException {
        return execute(ctx, StringArgumentType.getString(ctx, NAME), target, self);
    }

    protected int execute(CommandContext<CommandSourceStack> ctx, String name, GameProfile target, boolean self) throws CommandSyntaxException {
        PlayerData playerData = DataStorage.STORAGE.getOfflinePlayerData(ctx, target);
        Map<String, Home> homes = playerData.getHomes();
        if (!homes.containsKey(name)) {
            int limit = getHomesLimit(ctx.getSource());
            if (homes.size() >= limit) {
                ctx.getSource().sendFailure(Message.message("fabric-essentials.commands.sethome.limit"));
                return FAILURE;
            } else {
                Home home = new Home(new Location(ctx.getSource()));
                homes.put(name, home);
                DataStorage.STORAGE.saveOfflinePlayerData(ctx, target, playerData);
                sendQueryFeedbackWithOptionalTarget(ctx, target, self, home.placeholders(name), "fabric-essentials.commands.sethome");
                return SUCCESS;
            }
        } else {
            throw ALREADY_EXISTS.create();
        }
    }

    private int getHomesLimit(CommandSourceStack source) {
        if (predicate(PERMISSION_LIMIT, PERMISSION_LIMIT_BYPASS).test(source)) {
            return Integer.MAX_VALUE;
        } else {
            HomesConfig homesConfig = config().homes;
            int limit = homesConfig.defaultLimit;
            int added = 0;
            for (HomesLimit homesLimit : homesConfig.homesLimits) {
                if (predicate(PERMISSION_LIMIT, homesLimit.permission).test(source)) {
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
