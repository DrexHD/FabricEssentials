package org.server_utilities.essentials.command.util;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Properties;

public abstract class OptionalOnlineTargetCommand extends OptionalTargetCommand<EntitySelector, ServerPlayer> {

    public OptionalOnlineTargetCommand(Properties properties) {
        super(properties);
    }

    public OptionalOnlineTargetCommand(Properties properties, String targetArgumentId) {
        super(properties, targetArgumentId);
    }

    @Override
    protected ArgumentType<EntitySelector> getArgumentType() {
        return EntityArgument.player();
    }

    @Override
    protected ServerPlayer getArgument(CommandContext<CommandSourceStack> ctx, String string) throws CommandSyntaxException {
        return EntityArgument.getPlayer(ctx, string);
    }

}
