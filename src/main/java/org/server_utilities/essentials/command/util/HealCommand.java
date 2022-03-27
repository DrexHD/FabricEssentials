package org.server_utilities.essentials.command.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.SelfAndTargetCommand;

public class HealCommand extends SelfAndTargetCommand {

    public HealCommand() {
        super(Properties.create("heal").permission("heal"));
    }

    @Override
    protected int onSelf(CommandContext<CommandSourceStack> ctx, ServerPlayer sender) {
        ctx.getSource().sendSuccess(new TranslatableComponent("text.fabric-essentials.command.heal.self"), false);
        return heal(sender);
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, ServerPlayer sender, ServerPlayer target) {
        ctx.getSource().sendSuccess(new TranslatableComponent("text.fabric-essentials.command.heal.other", target.getDisplayName()), false);
        target.sendMessage(new TranslatableComponent("text.fabric-essentials.command.heal.healed", sender.getDisplayName()), Util.NIL_UUID);
        return heal(target);
    }

    private int heal(ServerPlayer target) {
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        target.setHealth(maxHealth);
        return (int) (maxHealth - health);
    }
}
