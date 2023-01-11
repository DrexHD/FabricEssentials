package org.server_utilities.essentials.command.impl.misc;

import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.CommandProperties;

public class FeedCommand extends SimpleTargetCommand {

    public FeedCommand() {
        super(CommandProperties.create("feed", 2), "fabric-essentials.commands.feed");
    }

    @Override
    protected int execute(ServerPlayer target) {
        int foodLevel = target.getFoodData().getFoodLevel();
        target.getFoodData().setFoodLevel(20);
        target.getFoodData().setSaturation(10);
        target.getFoodData().setExhaustion(0);
        return 20 - foodLevel;
    }
}
