package org.server_utilities.essentials.util.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;

public class DummyCraftingMenu extends CraftingMenu {

    public DummyCraftingMenu(int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(i, inventory, containerLevelAccess);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
