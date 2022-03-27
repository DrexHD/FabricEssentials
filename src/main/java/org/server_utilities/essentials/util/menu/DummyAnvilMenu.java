package org.server_utilities.essentials.util.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class DummyAnvilMenu extends AnvilMenu {

    public DummyAnvilMenu(int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(i, inventory, containerLevelAccess);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
