package org.server_utilities.essentials.command.impl.teleportation.home;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.*;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOfflineTargetCommand;
import org.server_utilities.essentials.storage.UserData;
import org.server_utilities.essentials.util.teleportation.Home;
import org.server_utilities.essentials.util.ComponentUtil;

import java.util.List;

public class HomesCommand extends OptionalOfflineTargetCommand {

    public HomesCommand() {
        super(Properties.create("homes").permission("homes"));
    }

    @Override
    protected int onSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return sendHomeList(ctx, ctx.getSource().getPlayerOrException().getGameProfile(), true);
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, GameProfile target) {
        return sendHomeList(ctx, target, false);
    }

    private int sendHomeList(CommandContext<CommandSourceStack> ctx, GameProfile target, boolean self) {
        UserData dataStorage = getEssentialsDataStorage(ctx).getUserData(target.getId());
        List<Home> homes = dataStorage.getHomes();
        if (homes.isEmpty()) {
            sendFeedback(ctx,
                    String.format("text.fabric-essentials.command.homes.%s.no_home", self ? "self" : "other"),
                    self ? new Object[]{} : new Object[]{target.getName()}
            );
        } else {
            sendFeedback(ctx,
                    String.format("text.fabric-essentials.command.homes.%s", self ? "self" : "other"),
                    self ? new Object[]{} : new Object[]{target.getName()}
            );
            Component[] components = homes.stream().map(home -> new TextComponent(home.getName()).withStyle(
                    Style.EMPTY
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("text.fabric-essentials.command.homes.hover")))
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/%s %s", HomeCommand.HOME_COMMAND, home.getName()) + (self ? "" : " " + target.getName())))
            )).toArray(Component[]::new);
            ctx.getSource().sendSuccess(ComponentUtil.join(components), false);
        }
        return homes.size();
    }
}
