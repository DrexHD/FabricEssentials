package me.drex.essentials.command.impl.misc.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.storage.DataStorage;
import me.drex.essentials.storage.PlayerData;

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
        PlayerData playerData = DataStorage.updatePlayerData(ctx.getSource().getPlayerOrException());
        playerData.commandSpy = !playerData.commandSpy;
        ctx.getSource().sendSuccess(() -> localized(playerData.commandSpy ? "fabric-essentials.commands.commandspy.enable" : "fabric-essentials.commands.commandspy.disable"), false);
        return playerData.commandSpy ? 1 : 0;
    }
}
