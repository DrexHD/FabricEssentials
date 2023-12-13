package org.server_utilities.essentials.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;

import java.util.Map;

import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.getFloat;
import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;

public class FlySpeedCommand extends Command {

    public FlySpeedCommand() {
        super(CommandProperties.create("flyspeed", 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
            argument("flyspeed", floatArg())
                .then(
                    argument("target", EntityArgument.player())
                        .executes(ctx -> setFlySpeed(ctx.getSource(), getFloat(ctx, "flyspeed"), EntityArgument.getPlayer(ctx, "target"), false))
                ).executes(ctx -> setFlySpeed(ctx.getSource(), getFloat(ctx, "flyspeed"), ctx.getSource().getPlayerOrException(), true))
        ).executes(ctx -> setFlySpeed(ctx.getSource(), 0.05f, ctx.getSource().getPlayerOrException(), true));
    }

    private int setFlySpeed(CommandSourceStack src, float flySpeed, ServerPlayer target, boolean self) {
        Map<String, Component> placeholders = Map.of("fly_speed", Component.literal(String.valueOf(flySpeed)));
        if (self) {
            src.sendSuccess(() -> localized("fabric-essentials.commands.flyspeed.self", placeholders), false);
        } else {
            src.sendSuccess(() -> localized("fabric-essentials.commands.flyspeed.other", placeholders, PlaceholderContext.of(target)), false);
        }
        target.getAbilities().setFlyingSpeed(flySpeed);
        target.onUpdateAbilities();
        return SUCCESS;
    }

}
