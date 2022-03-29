package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOnlineTargetCommand;

public class HealCommand extends OptionalOnlineTargetCommand {

    public HealCommand() {
        super(Properties.create("heal").permission("heal"));
    }

    @Override
    protected int onSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return heal(ctx, ctx.getSource().getPlayerOrException(), true);
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        return heal(ctx, target, false);
    }

    private int heal(CommandContext<CommandSourceStack> ctx, ServerPlayer target, boolean self) {
        sendFeedback(ctx,
                String.format("text.fabric-essentials.command.heal.%s", self ? "self" : "other"),
                self ? new Object[]{} : new Object[]{target.getDisplayName()}
        );
        if (!self) sendFeedback(target,
                String.format("text.fabric-essentials.command.heal.%s", "victim"),
                toName(ctx)
        );
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        target.setHealth(maxHealth);
        return (int) (maxHealth - health);
    }
}
