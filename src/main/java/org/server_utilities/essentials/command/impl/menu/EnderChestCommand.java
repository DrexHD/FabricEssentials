package org.server_utilities.essentials.command.impl.menu;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import org.server_utilities.essentials.command.Properties;

public class EnderChestCommand extends SimpleMenuCommand {

    private static final MutableComponent ENDERCHEST_TITLE = Component.translatable("container.enderchest");

    public EnderChestCommand() {
        super(Properties.create("enderchest", "ec"));
    }

    @Override
    protected MenuProvider createMenu(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        return new SimpleMenuProvider((i, inventory, player) -> ChestMenu.threeRows(i, inventory, target.getEnderChestInventory()), ENDERCHEST_TITLE);
    }
}