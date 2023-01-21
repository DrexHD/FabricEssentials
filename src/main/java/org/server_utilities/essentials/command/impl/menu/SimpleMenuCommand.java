package org.server_utilities.essentials.command.impl.menu;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;

public abstract class SimpleMenuCommand extends Command {

    public SimpleMenuCommand(CommandProperties commandProperties) {
        super(commandProperties);
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.executes(ctx -> {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            player.openMenu(createMenu(player));
            return SUCCESS;
        });
    }

    protected abstract MenuProvider createMenu(ServerPlayer target) throws CommandSyntaxException;

}
