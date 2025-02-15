package me.drex.essentials.command.impl.teleportation;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.config.Config;
import me.drex.essentials.config.ConfigManager;
import me.drex.essentials.storage.DataStorage;
import me.drex.essentials.storage.PlayerData;
import me.drex.essentials.util.teleportation.Location;
import me.drex.message.api.LocalizedMessage;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

import static me.drex.message.api.LocalizedMessage.localized;

public class BackCommand extends Command {

    public BackCommand() {
        super(CommandProperties.create("back", 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.executes(this::teleportBack);
    }

    private int teleportBack(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var source = ctx.getSource();
        var target = source.getPlayerOrException();
        PlayerData playerData = DataStorage.getPlayerData(target);
        if (playerData.teleportLocations.isEmpty()) {
            source.sendFailure(localized("fabric-essentials.commands.back.empty"));
            return FAILURE;
        }
        Location location = playerData.teleportLocations.pop();
        source.sendSystemMessage(
            LocalizedMessage.builder("fabric-essentials.commands.back")
                .addPlaceholders(location.placeholders())
                .build()
        );
        location.teleport(target, ConfigManager.config().teleportation.saveBackCommandLocation);
        return SUCCESS;
    }

}
