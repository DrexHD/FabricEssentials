package org.server_utilities.essentials.command.util;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Properties;

public abstract class OptionalOnlineTargetCommand extends OptionalTargetCommand<EntitySelector, ServerPlayer> {

    public OptionalOnlineTargetCommand(Properties properties) {
        super(properties);
    }

    @Override
    protected ArgumentType<EntitySelector> getArgumentType() {
        return EntityArgument.player();
    }

    @Override
    protected ServerPlayer getSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return ctx.getSource().getPlayerOrException();
    }

    @Override
    protected void sendTargetFeedback(ServerPlayer target, MinecraftServer server, Component component) {
        target.sendSystemMessage(component);
    }

    @Override
    protected ServerPlayer getArgument(CommandContext<CommandSourceStack> ctx, String string) throws CommandSyntaxException {
        return EntityArgument.getPlayer(ctx, string);
    }

    @Override
    protected PlaceholderContext getTargetPlaceholderContext(ServerPlayer target, MinecraftServer unused) {
        return PlaceholderContext.of(target);
    }

}
