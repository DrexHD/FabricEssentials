package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOnlineTargetCommand;

public class FeedCommand extends OptionalOnlineTargetCommand {

    private static final int MAX_FOOD = 20;

    public FeedCommand() {
        super(Properties.create("feed"));
    }

    @Override
    protected int execute(CommandContext<CommandSourceStack> ctx, ServerPlayer target, boolean self) throws CommandSyntaxException {
        sendFeedbackWithOptionalTarget(ctx, target, self, "fabric-essentials.commands.feed");
        int foodLevel = target.getFoodData().getFoodLevel();
        target.getFoodData().setFoodLevel(MAX_FOOD);
        target.getFoodData().setSaturation(MAX_FOOD);
        return MAX_FOOD - foodLevel;
    }

}
