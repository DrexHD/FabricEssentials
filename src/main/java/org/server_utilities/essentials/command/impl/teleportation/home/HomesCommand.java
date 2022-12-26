package org.server_utilities.essentials.command.impl.teleportation.home;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.drex.message.api.Message;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOfflineTargetCommand;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;
import org.server_utilities.essentials.util.teleportation.Home;

import java.util.HashMap;
import java.util.Map;

public class HomesCommand extends OptionalOfflineTargetCommand {

    public HomesCommand() {
        super(Properties.create("homes"));
    }

    @Override
    protected int execute(CommandContext<CommandSourceStack> ctx, GameProfile target, boolean self) throws CommandSyntaxException {
        PlayerData dataStorage = DataStorage.STORAGE.getOfflinePlayerData(ctx, target);
        Map<String, Home> homes = dataStorage.getHomes();
        if (homes.isEmpty()) {
            sendQueryFeedbackWithOptionalTarget(ctx, target, self, "fabric-essentials.commands.homes.empty");
        } else {
            Component homesList = ComponentUtils.formatList(homes.entrySet(), Message.message("fabric-essentials.commands.homes.list.separator"), entry -> {
                return Message.message("fabric-essentials.commands.homes.list.element", entry.getValue().placeholders(entry.getKey()), getTargetPlaceholderContext(target, ctx.getSource().getServer()));
            });
            sendQueryFeedbackWithOptionalTarget(ctx, target, self, new HashMap<>() {{
                put("home_list", homesList);
                put("home_count", Component.literal(String.valueOf(homes.size())));
            }}, "fabric-essentials.commands.homes");
        }
        return homes.size();
    }

}
