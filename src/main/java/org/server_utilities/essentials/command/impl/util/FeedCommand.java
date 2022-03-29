package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOnlineTargetCommand;

public class FeedCommand extends OptionalOnlineTargetCommand {

    private static final int MAX_FOOD = 20;

    public FeedCommand() {
        super(Properties.create("feed").permission("feed"));
    }

    @Override
    protected int onSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return feed(ctx, ctx.getSource().getPlayerOrException(), true);
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        return feed(ctx, target, false);
    }

    private int feed(CommandContext<CommandSourceStack> ctx, ServerPlayer target, boolean self) {
        sendFeedback(ctx,
                String.format("text.fabric-essentials.command.feed.%s", self ? "self" : "other"),
                self ? new Object[]{} : new Object[]{target.getDisplayName()}
        );
        if (!self) sendFeedback(target,
                String.format("text.fabric-essentials.command.feed.%s", "victim"),
                toName(ctx)
        );
        int foodLevel = target.getFoodData().getFoodLevel();
        target.getFoodData().setFoodLevel(MAX_FOOD);
        target.getFoodData().setSaturation(MAX_FOOD);
        return MAX_FOOD - foodLevel;
    }
}
