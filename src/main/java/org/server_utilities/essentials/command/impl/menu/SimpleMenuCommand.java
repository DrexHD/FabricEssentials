package org.server_utilities.essentials.command.impl.menu;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOnlineTargetCommand;

public abstract class SimpleMenuCommand extends OptionalOnlineTargetCommand {

    public SimpleMenuCommand(Properties properties) {
        super(properties);
    }

    @Override
    protected int onSelf(CommandContext<CommandSourceStack> ctx, ServerPlayer sender) {
        sender.openMenu(createMenu(ctx, sender, sender));
        return 1;
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, ServerPlayer sender, ServerPlayer target) {
        target.openMenu(createMenu(ctx, sender, target));
        return 1;
    }

    protected abstract MenuProvider createMenu(CommandContext<CommandSourceStack> ctx, ServerPlayer sender, ServerPlayer target);

}
