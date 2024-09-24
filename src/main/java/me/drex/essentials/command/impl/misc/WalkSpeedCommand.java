package me.drex.essentials.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;

import java.util.Map;

import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.getFloat;
import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;

public class WalkSpeedCommand extends Command {

    public WalkSpeedCommand() {
        super(CommandProperties.create("walkspeed", 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
            argument("walkspeed", floatArg())
                .then(
                    argument("target", EntityArgument.player())
                        .executes(ctx -> setFlySpeed(ctx.getSource(), getFloat(ctx, "walkspeed"), EntityArgument.getPlayer(ctx, "target"), false))
                ).executes(ctx -> setFlySpeed(ctx.getSource(), getFloat(ctx, "walkspeed"), ctx.getSource().getPlayerOrException(), true))
        ).executes(ctx -> setFlySpeed(ctx.getSource(), 0.1f, ctx.getSource().getPlayerOrException(), true));
    }

    private int setFlySpeed(CommandSourceStack src, float walkSpeed, ServerPlayer target, boolean self) {
        Map<String, Component> placeholders = Map.of("walk_speed", Component.literal(String.valueOf(walkSpeed)));
        if (self) {
            src.sendSuccess(() -> localized("fabric-essentials.commands.walkspeed.self", placeholders), false);
        } else {
            src.sendSuccess(() -> localized("fabric-essentials.commands.walkspeed.other", placeholders, PlaceholderContext.of(target)), false);
        }
        target.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(walkSpeed);
        return SUCCESS;
    }

}
