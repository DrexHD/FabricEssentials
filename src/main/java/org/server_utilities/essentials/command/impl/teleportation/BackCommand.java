package org.server_utilities.essentials.command.impl.teleportation;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;
import org.server_utilities.essentials.util.teleportation.Location;

import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;

public class BackCommand extends Command {

    public BackCommand() {
        super(CommandProperties.create("back", 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
            argument("target", EntityArgument.player())
                .executes(ctx -> teleportBack(ctx.getSource(), EntityArgument.getPlayer(ctx, "target")))
            ).executes(ctx -> teleportBack(ctx.getSource(), ctx.getSource().getPlayerOrException()));
    }

    private int teleportBack(CommandSourceStack src, ServerPlayer target) {
        PlayerData playerData = DataStorage.updatePlayerData(target);
        if (playerData.teleportLocations.isEmpty()) {
            src.sendFailure(localized(""));
            return FAILURE;
        }
        Location location = playerData.teleportLocations.pop();
        location.teleport(target, false);
        return SUCCESS;
    }

}
