package org.server_utilities.essentials.command.impl.home;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;
import org.server_utilities.essentials.util.teleportation.Home;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.arguments.GameProfileArgument.gameProfile;
import static org.server_utilities.essentials.command.impl.home.HomeCommand.*;
import static org.server_utilities.essentials.command.util.CommandUtil.PROFILES_PROVIDER;
import static org.server_utilities.essentials.command.util.CommandUtil.getGameProfile;

public class DeleteHomeCommand extends Command {

    public DeleteHomeCommand() {
        super(CommandProperties.create("deletehome", new String[]{"delhome", "removehome"}, 0));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal
                .then(
                        argument("home", word()).suggests(HOMES_PROVIDER)
                                .then(argument("player", gameProfile()).suggests(PROFILES_PROVIDER)
                                        .requires(require("other"))
                                        .executes(ctx -> deleteHome(ctx.getSource(), getString(ctx, "home"), getGameProfile(ctx, "player"), false))
                                )
                                .executes(ctx -> deleteHome(ctx.getSource(), getString(ctx, "home"), ctx.getSource().getPlayerOrException().getGameProfile(), true))
                )
                .executes(ctx -> deleteHome(ctx.getSource(), DEFAULT_HOME_NAME, ctx.getSource().getPlayerOrException().getGameProfile(), true));
    }

    protected int deleteHome(CommandSourceStack src, String name, GameProfile target, boolean self) throws CommandSyntaxException {
        PlayerData playerData = DataStorage.getOfflinePlayerData(src.getServer(), target.getId());
        Home home = playerData.homes.get(name);
        if (home != null) {
            playerData.homes.remove(name);
            DataStorage.updateOfflinePlayerData(src.getServer(), target.getId(), playerData);
            if (self) {
                src.sendSystemMessage(localized("fabric-essentials.commands.deletehome.self", home.placeholders(name)));
            } else {
                src.sendSystemMessage(localized("fabric-essentials.commands.deletehome.other", home.placeholders(name), PlaceholderContext.of(target, src.getServer())));
            }
            return SUCCESS;
        } else {
            throw UNKNOWN.create();
        }
    }

}
