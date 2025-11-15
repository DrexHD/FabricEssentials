package me.drex.essentials.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.arguments.EntityArgument.getPlayer;
import static net.minecraft.commands.arguments.EntityArgument.player;

public class RepairCommand extends Command {

    public RepairCommand() {
        super(CommandProperties.create("repair", 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
                Commands.literal("all")
                    .requires(require("all"))
                    .executes(ctx -> {
                        CommandSourceStack src = ctx.getSource();
                        ServerPlayer player = src.getPlayerOrException();
                        //? if >= 1.21.5 {
                        for (ItemStack itemStack : player.getInventory()) {
                        //?} else {
                        /*for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                            ItemStack itemStack = player.getInventory().getItem(i);
                            *///?}
                            if (itemStack.isDamaged()) {
                                itemStack.setDamageValue(0);
                            }
                        }
                        src.sendSuccess(() -> localized("fabric-essentials.commands.repair.all"), false);
                        return SUCCESS;
                    })
            )
            .executes(ctx -> {
                CommandSourceStack src = ctx.getSource();
                ServerPlayer player = src.getPlayerOrException();
                ItemStack itemStack = player.getMainHandItem();
                if (itemStack.isEmpty() || !itemStack.isDamaged()) {
                    src.sendFailure(localized("fabric-essentials.commands.repair.missing"));
                    return FAILURE;
                }
                itemStack.setDamageValue(0);
                src.sendSuccess(() -> localized("fabric-essentials.commands.repair"), false);
                return SUCCESS;
            });
    }
}
