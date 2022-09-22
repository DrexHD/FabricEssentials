package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOnlineTargetCommand;

public class PingCommand extends OptionalOnlineTargetCommand {

    public PingCommand() {
        super(Properties.create("ping"));
    }

    @Override
    protected int execute(CommandContext<CommandSourceStack> ctx, ServerPlayer target, boolean self) throws CommandSyntaxException {
        sendQueryFeedbackWithOptionalTarget(ctx, self, new Object[]{target.latency}, new Object[]{target.getDisplayName(), target.latency});
        return target.latency;
    }

}
