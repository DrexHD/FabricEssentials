package me.drex.essentials.command.impl.misc;

import net.minecraft.server.level.ServerPlayer;
import me.drex.essentials.command.CommandProperties;

public class HealCommand extends SimpleTargetCommand {

    public HealCommand() {
        super(CommandProperties.create("heal", 2), "fabric-essentials.commands.heal");
    }

    @Override
    protected int execute(ServerPlayer target) {
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        target.setHealth(maxHealth);
        target.getFoodData().setFoodLevel(20);
        return (int) (maxHealth - health);
    }
}
