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
import org.server_utilities.essentials.util.StyledInputUtil;

import java.util.function.Predicate;

public class StaffMessageCommand extends Command {

    public static final Predicate<CommandSourceStack> PERMISSION = Command.permission("staff");

    public StaffMessageCommand() {
        super(Properties.create("staffmessage", "sm").andPredicate(PERMISSION));
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
        for (ServerPlayer player : ctx.getSource().getServer().getPlayerList().getPlayers()) {
            CommandSourceStack commandSourceStack = player.createCommandSourceStack();
            if (PERMISSION.test(commandSourceStack)) {
                player.sendSystemMessage(Component.translatable("text.fabric-essentials.command.chat.channel",
                        Component.translatable("text.fabric-essentials.command.chat.channel.staff"),
                        ctx.getSource().getDisplayName(),
                        StyledInputUtil.parse(message, textTag -> Command.permission("style", "staff", textTag.name()).test(commandSourceStack))));
            }
        }
        return 1;
    }

}
