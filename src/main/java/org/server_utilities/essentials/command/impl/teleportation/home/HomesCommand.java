package org.server_utilities.essentials.command.impl.teleportation.home;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.*;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOfflineTargetCommand;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;
import org.server_utilities.essentials.util.teleportation.Home;

import java.util.List;

public class HomesCommand extends OptionalOfflineTargetCommand {

    public HomesCommand() {
        super(Properties.create("homes").permission("homes"));
    }

    @Override
    protected int execute(CommandContext<CommandSourceStack> ctx, GameProfile target, boolean self) throws CommandSyntaxException {
        PlayerData dataStorage = DataStorage.STORAGE.getOfflinePlayerData(ctx, target);
        List<Home> homes = dataStorage.getHomes();
        if (homes.isEmpty()) {
            sendQueryFeedbackWithOptionalTarget(ctx, self, EMPTY, new Object[]{target.getName()}, "empty");
        } else {
            sendQueryFeedbackWithOptionalTarget(ctx, self, EMPTY, new Object[]{target.getName()});
            Component homesComponent = ComponentUtils.formatList(homes.stream().map(Home::name).toList(), name -> Component.literal(name).withStyle(
                    Style.EMPTY
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(translation("hover"))))
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/%s %s", HomeCommand.HOME_COMMAND, name) + (self ? "" : " " + target.getName())))
            ));
            ctx.getSource().sendSuccess(homesComponent, false);
        }
        return homes.size();
    }

}
