package org.server_utilities.essentials.command.impl.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import org.server_utilities.essentials.command.CommandProperties;

public class EnderChestCommand extends SimpleMenuCommand {

    private static final MutableComponent ENDERCHEST_TITLE = Component.translatable("container.enderchest");

    public EnderChestCommand() {
        super(CommandProperties.create("enderchest", new String[]{"ec"}, 2));
    }

    @Override
    protected MenuProvider createMenu(ServerPlayer target) {
        return new SimpleMenuProvider((i, inventory, player) -> ChestMenu.threeRows(i, inventory, target.getEnderChestInventory()), ENDERCHEST_TITLE);
    }
}