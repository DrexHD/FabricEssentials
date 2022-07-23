package org.server_utilities.essentials.command.impl.teleportation.home;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOfflineTargetCommand;
import org.server_utilities.essentials.config.homes.HomesConfig;
import org.server_utilities.essentials.config.homes.HomesLimit;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.UserData;
import org.server_utilities.essentials.util.teleportation.Home;
import org.server_utilities.essentials.util.teleportation.Location;

import java.util.List;
import java.util.Optional;

public class SetHomeCommand extends OptionalOfflineTargetCommand {

    private static final SimpleCommandExceptionType ALREADY_EXISTS = new SimpleCommandExceptionType(Component.translatable("text.fabric-essentials.command.sethome.already_exists"));
    private static final String NAME = "name";
    private static final String PERMISSION_BASE = "homes";
    private static final String PERMISSION_LIMIT = "limit";
    private static final String PERMISSION_LIMIT_BYPASS = "bypass";

    public SetHomeCommand() {
        super(Properties.create("sethome").permission("sethome"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        RequiredArgumentBuilder<CommandSourceStack, String> name = Commands.argument(NAME, StringArgumentType.string());
        registerOptionalArgument(name);
        literal.then(name);
    }

    @Override
    protected int onSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return setHome(ctx, StringArgumentType.getString(ctx, NAME), ctx.getSource().getPlayerOrException().getGameProfile(), true);
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, GameProfile target) throws CommandSyntaxException {
        return setHome(ctx, StringArgumentType.getString(ctx, NAME), target, false);
    }

    private int setHome(CommandContext<CommandSourceStack> ctx, String name, GameProfile target, boolean self) throws CommandSyntaxException {
        ServerPlayer serverPlayer = ctx.getSource().getPlayerOrException();
        DataStorage dataStorage = DataStorage.STORAGE;
        UserData userData = dataStorage.getPlayerData(ctx.getSource().getServer(), target.getId());
        Optional<Home> optional = userData.getHome(name);
        if (optional.isEmpty()) {
            List<Home> homes = userData.getHomes();
            int limit = getHomesLimit(ctx.getSource());
            if (homes.size() >= limit) {
                ctx.getSource().sendFailure(Component.translatable("text.fabric-essentials.command.sethome.limit"));
                return 0;
            } else {
                Home newHome = new Home(name, new Location(serverPlayer));
                homes.add(newHome);
                dataStorage.savePlayerData(ctx.getSource().getServer(), target.getId(), userData);
                sendFeedback(ctx,
                        String.format("text.fabric-essentials.command.sethome.%s", self ? "self" : "other"),
                        self ? new Object[]{name} : new Object[]{name, target.getName()}
                );
                return 1;
            }
        } else {
            throw ALREADY_EXISTS.create();
        }
    }

    private int getHomesLimit(CommandSourceStack source) {
        if (permission(PERMISSION_BASE, PERMISSION_LIMIT, PERMISSION_LIMIT_BYPASS).test(source)) {
            return Integer.MAX_VALUE;
        } else {
            HomesConfig homesConfig = getConfig().homesConfig;
            int limit = homesConfig.defaultLimit;
            int added = 0;
            for (HomesLimit homesLimit : homesConfig.homesLimits) {
                if (permission(PERMISSION_BASE, PERMISSION_LIMIT, homesLimit.permission).test(source)) {
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
