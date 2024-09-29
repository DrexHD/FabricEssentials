package me.drex.essentials.command.impl.misc;

import me.drex.essentials.mixin.FoodDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import me.drex.essentials.command.CommandProperties;

public class FeedCommand extends SimpleTargetCommand {

    public FeedCommand() {
        super(CommandProperties.create("feed", 2), "fabric-essentials.commands.feed");
    }

    @Override
    protected int execute(ServerPlayer target) {
        int foodLevel = target.getFoodData().getFoodLevel();
        target.getFoodData().setFoodLevel(20);
        target.getFoodData().setSaturation(10);
        ((FoodDataAccessor)target.getFoodData()).setExhaustionLevel(0);
        return 20 - foodLevel;
    }
}
