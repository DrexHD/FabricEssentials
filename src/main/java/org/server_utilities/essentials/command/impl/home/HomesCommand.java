package org.server_utilities.essentials.command.impl.home;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;
import org.server_utilities.essentials.util.teleportation.Home;

import java.util.HashMap;
import java.util.Map;

import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.arguments.GameProfileArgument.gameProfile;
import static org.server_utilities.essentials.command.util.CommandUtil.PROFILES_PROVIDER;
import static org.server_utilities.essentials.command.util.CommandUtil.getGameProfile;

public class HomesCommand extends Command {

    public HomesCommand() {
        super(CommandProperties.create("homes", 0));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
                argument("player", gameProfile()).suggests(PROFILES_PROVIDER)
                        .requires(require("other"))
                        .executes(ctx -> listOtherHomes(ctx.getSource(), getGameProfile(ctx, "player")))
        ).executes(ctx -> listHomes(ctx.getSource()));
    }

    protected int listHomes(CommandSourceStack src) throws CommandSyntaxException {
        PlayerData dataStorage = DataStorage.getPlayerData(src.getPlayerOrException());
        Map<String, Home> homes = dataStorage.homes;
        if (homes.isEmpty()) {
            src.sendSystemMessage(localized("fabric-essentials.commands.homes.self.empty"));
        } else {
            Component homesList = ComponentUtils.formatList(homes.entrySet(), localized("fabric-essentials.commands.homes.self.list.separator"), entry -> {
                return localized("fabric-essentials.commands.homes.self.list.element", entry.getValue().placeholders(entry.getKey()));
            });
            src.sendSystemMessage(localized("fabric-essentials.commands.homes.self", new HashMap<>() {{
                put("home_list", homesList);
                put("home_count", Component.literal(String.valueOf(homes.size())));
            }}));

        }
        return homes.size();
    }

    protected int listOtherHomes(CommandSourceStack src, GameProfile target) {
        PlayerData dataStorage = DataStorage.getOfflinePlayerData(src.getServer(), target.getId());
        Map<String, Home> homes = dataStorage.homes;
        if (homes.isEmpty()) {
            src.sendSystemMessage(localized("fabric-essentials.commands.homes.other.empty", PlaceholderContext.of(target, src.getServer())));
        } else {
            Component homesList = ComponentUtils.formatList(homes.entrySet(), localized("fabric-essentials.commands.homes.other.list.separator"), entry -> {
                return localized("fabric-essentials.commands.homes.other.list.element", entry.getValue().placeholders(entry.getKey()), PlaceholderContext.of(target, src.getServer()));
            });
            src.sendSystemMessage(localized("fabric-essentials.commands.homes.other", new HashMap<>() {{
                put("home_list", homesList);
                put("home_count", Component.literal(String.valueOf(homes.size())));
            }}, PlaceholderContext.of(target, src.getServer())));
        }
        return homes.size();
    }

}
