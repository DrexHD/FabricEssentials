package org.server_utilities.essentials.command.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.SelfAndTargetCommand;

public class PingCommand extends SelfAndTargetCommand {

    public PingCommand() {
        super(Properties.create("ping").permission("ping"));
    }

    @Override
    protected int onSelf(CommandContext<CommandSourceStack> ctx, ServerPlayer sender) {
        ctx.getSource().sendSuccess(new TranslatableComponent("text.fabric-essentials.command.ping.self", sender.latency), false);
        return sender.latency;
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, ServerPlayer sender, ServerPlayer target) {
        ctx.getSource().sendSuccess(new TranslatableComponent("text.fabric-essentials.command.ping.other", target.getDisplayName(), target.latency), false);
        return target.latency;
    }
}
