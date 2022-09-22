package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOnlineTargetCommand;

public class HealCommand extends OptionalOnlineTargetCommand {

    public HealCommand() {
        super(Properties.create("heal"));
    }

    @Override
    protected int execute(CommandContext<CommandSourceStack> ctx, ServerPlayer target, boolean self) throws CommandSyntaxException {
        sendFeedbackWithOptionalTarget(ctx, target, self, EMPTY, new Object[]{target.getDisplayName()}, new Object[]{ctx.getSource().getDisplayName()});
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        target.setHealth(maxHealth);
        return (int) (maxHealth - health);
    }
}
