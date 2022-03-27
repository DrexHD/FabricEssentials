package org.server_utilities.essentials.command.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.SelfAndTargetCommand;

public class FeedCommand extends SelfAndTargetCommand {

    public FeedCommand() {
        super(Properties.create("feed").permission("feed"));
    }

    @Override
    protected int onSelf(CommandContext<CommandSourceStack> ctx, ServerPlayer sender) {
        ctx.getSource().sendSuccess(new TranslatableComponent("text.fabric-essentials.command.feed.self"), false);
        return feed(sender);
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, ServerPlayer sender, ServerPlayer target) {
        ctx.getSource().sendSuccess(new TranslatableComponent("text.fabric-essentials.command.feed.other", target.getDisplayName()), false);
        target.sendMessage(new TranslatableComponent("text.fabric-essentials.command.feed.healed", sender.getDisplayName()), Util.NIL_UUID);
        return feed(target);
    }

    private int feed(ServerPlayer target) {
        int foodLevel = target.getFoodData().getFoodLevel();
        int maxFood = 20;
        target.getFoodData().setFoodLevel(maxFood);
        return maxFood - foodLevel;
    }
}
