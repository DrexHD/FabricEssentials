package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOnlineTargetCommand;

public class PingCommand extends OptionalOnlineTargetCommand {

    public PingCommand() {
        super(Properties.create("ping").permission("ping"));
    }

    @Override
    protected int onSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer sender = ctx.getSource().getPlayerOrException();
        sendFeedback(ctx, "text.fabric-essentials.command.ping.self", sender.latency);
        return sender.latency;
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        sendFeedback(ctx, "text.fabric-essentials.command.ping.other", target.getDisplayName(), target.latency);
        return target.latency;
    }
}
