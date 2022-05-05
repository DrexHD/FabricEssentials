package org.server_utilities.essentials.util.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LinkedInventory extends Inventory {

    private final Inventory targetInventory;

    public LinkedInventory(Player target) {
        super(target);
        targetInventory = target.getInventory();
    }

    @Override
    public int getContainerSize() {
        return 45;
    }

    @Override
    public ItemStack getItem(int slot) {
        return targetInventory.getItem(slot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        targetInventory.setItem(slot, stack);
    }

    @Override
    public void setChanged() {
        targetInventory.setChanged();
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return targetInventory.removeItem(i, j);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
