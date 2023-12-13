package org.server_utilities.essentials.command.impl.misc.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;

import static me.drex.message.api.LocalizedMessage.localized;

public class CommandSpyCommand extends Command {

    public CommandSpyCommand() {
        super(CommandProperties.create("commandspy", 3));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.executes(this::execute);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        PlayerData playerData = DataStorage.getAndSavePlayerData(ctx.getSource().getPlayerOrException());
        playerData.commandSpy = !playerData.commandSpy;
        ctx.getSource().sendSuccess(() -> localized(playerData.commandSpy ? "fabric-essentials.commands.commandspy.enable" : "fabric-essentials.commands.commandspy.disable"), false);
        return playerData.commandSpy ? 1 : 0;
    }
}
