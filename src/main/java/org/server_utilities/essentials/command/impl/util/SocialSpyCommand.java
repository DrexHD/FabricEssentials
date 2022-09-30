package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.PlayerData;

public class SocialSpyCommand extends Command {

    public SocialSpyCommand() {
        super(Properties.create("socialspy"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.executes(this::execute);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        PlayerData playerData = DataStorage.STORAGE.getAndSavePlayerData(ctx.getSource().getPlayerOrException());
        playerData.socialSpy = !playerData.socialSpy;
        sendSuccess(ctx.getSource(), playerData.socialSpy ? "enable" : "disable");
        return playerData.socialSpy ? 1 : 0;
    }
}
