package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.util.KeyUtil;
import org.server_utilities.essentials.util.StyledInputUtil;

public class StaffMessageCommand extends Command {

    public StaffMessageCommand() {
        super(Properties.create("staffmessage", "sm"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> argument) {
        argument.then(
                Commands.argument("message", StringArgumentType.greedyString())
                        .executes(this::execute)
        );
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        String message = StringArgumentType.getString(ctx, "message");
        Component component = StyledInputUtil.parse(message, textTag -> KeyUtil.permission(ctx.getSource(), "style.staff", textTag.name()));
        for (ServerPlayer player : ctx.getSource().getServer().getPlayerList().getPlayers()) {
            CommandSourceStack src = player.createCommandSourceStack();
            if (KeyUtil.permission(src, "isStaff")) {
                player.sendSystemMessage(Component.translatable("text.fabric-essentials.chat.channel",
                        Component.translatable("text.fabric-essentials.chat.channel.staff"),
                        ctx.getSource().getDisplayName(),
                        component
                ));
            }
        }
        return SUCCESS;
    }

}
