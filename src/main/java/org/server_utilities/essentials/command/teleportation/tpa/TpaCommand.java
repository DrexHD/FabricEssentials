package org.server_utilities.essentials.command.teleportation.tpa;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;

// TODO:
public class TpaCommand extends Command {

    private static final String TARGET_ARGUMENT_ID = "target";

    public TpaCommand() {
        super(Properties.create("tpa", "tpr").permission("tpa"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        RequiredArgumentBuilder<CommandSourceStack, EntitySelector> target = Commands.argument(TARGET_ARGUMENT_ID, EntityArgument.player());
    }

}
