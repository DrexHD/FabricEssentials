package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOnlineTargetCommand;

public class GlowCommand extends OptionalOnlineTargetCommand {

    public GlowCommand() {
        super(Properties.create("glow"));
    }

    @Override
    protected int execute(CommandContext<CommandSourceStack> ctx, ServerPlayer target, boolean self) throws CommandSyntaxException {
        sendFeedbackWithOptionalTarget(ctx, target, self, EMPTY, new Object[]{target.getDisplayName()}, new Object[]{ctx.getSource().getDisplayName()});
        boolean prev = target.hasGlowingTag();
        target.setGlowingTag(!prev);
        return prev ? FAILURE : SUCCESS;
    }

}
