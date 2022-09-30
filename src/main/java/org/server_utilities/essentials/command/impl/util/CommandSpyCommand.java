package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;

public class CommandSpyCommand extends Command {

    public CommandSpyCommand() {
        super(Properties.create("commandspy"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.executes(this::execute);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        PlayerData playerData = DataStorage.STORAGE.getAndSavePlayerData(ctx.getSource().getPlayerOrException());
        playerData.commandSpy = !playerData.commandSpy;
        sendSuccess(ctx.getSource(), playerData.commandSpy ? "enable" : "disable");
        return playerData.commandSpy ? 1 : 0;
    }
}
