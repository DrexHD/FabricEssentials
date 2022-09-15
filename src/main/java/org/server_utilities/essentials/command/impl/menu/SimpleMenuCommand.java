package org.server_utilities.essentials.command.impl.menu;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
    protected int execute(CommandContext<CommandSourceStack> ctx, ServerPlayer target, boolean self) throws CommandSyntaxException {
        target.openMenu(createMenu(ctx, target));
        return SUCCESS;
    }

    protected abstract MenuProvider createMenu(CommandContext<CommandSourceStack> ctx, ServerPlayer target) throws CommandSyntaxException;

}
