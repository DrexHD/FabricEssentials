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

public class HomeCommand extends OptionalOfflineTargetCommand {

    public static final SimpleCommandExceptionType UNKNOWN = new SimpleCommandExceptionType(Component.translatable("text.fabric-essentials.command.home.unknown"));
    private static final String NAME = "name";
    public static final String HOME_COMMAND = "home";

    public HomeCommand() {
        super(Properties.create(HOME_COMMAND));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        RequiredArgumentBuilder<CommandSourceStack, String> name = Commands.argument(NAME, StringArgumentType.string()).suggests(HOMES_PROVIDER);
        registerOptionalArgument(name);
        literal.then(name).executes(ctx -> execute(ctx, "home", getSelf(ctx), true));
    }

    @Override
    protected int execute(CommandContext<CommandSourceStack> ctx, GameProfile target, boolean self) throws CommandSyntaxException {
        return execute(ctx, StringArgumentType.getString(ctx, NAME), target, self);
    }

    protected int execute(CommandContext<CommandSourceStack> ctx, String name, GameProfile target, boolean self) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        ServerPlayer serverPlayer = src.getPlayerOrException();
        DataStorage dataStorage = DataStorage.STORAGE;
        PlayerData playerData = dataStorage.getOfflinePlayerData(ctx, target);
        Home home = playerData.getHomes().get(name);
        if (home == null) throw UNKNOWN.create();
        ServerLevel targetLevel = home.location().getLevel(src.getServer());
        if (targetLevel != null) {
            asyncTeleport(src, targetLevel, home.location().chunkPos(), config().homes.waitingPeriod).whenCompleteAsync((chunkAccessOptional, throwable) -> {
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

    public static final SuggestionProvider<CommandSourceStack> HOMES_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.suggest(DataStorage.STORAGE.getPlayerData(ctx).getHomes().keySet(), builder);

}
