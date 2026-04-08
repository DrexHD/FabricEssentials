package me.drex.essentials.util.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.jetbrains.annotations.NotNull;

public class DummyEnchantmentMenu extends EnchantmentMenu {

    public final int bookCases;

    public DummyEnchantmentMenu(int bookCases, int containerId, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(containerId, inventory, containerLevelAccess);
        this.bookCases = bookCases;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
