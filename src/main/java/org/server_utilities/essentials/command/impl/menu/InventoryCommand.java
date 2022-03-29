package org.server_utilities.essentials.command.impl.menu;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import org.server_utilities.essentials.command.Properties;

// TODO: Figure out a way to get around Inventory.stillValid()
public class InventoryCommand extends SimpleMenuCommand {

    private static final TranslatableComponent INVENTORY_TITLE = new TranslatableComponent("container.inventory");

    public InventoryCommand() {
        super(Properties.create("inv", "inventory").permission("inventory"));
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, ServerPlayer sender, ServerPlayer target) {
        sendFeedback(ctx, "text.fabric-essentials.command.inventory.menu.other", target.getDisplayName(), INVENTORY_TITLE);
        return super.onOther(ctx, sender, target);
    }

    @Override
    protected MenuProvider createMenu(CommandContext<CommandSourceStack> ctx, ServerPlayer sender, ServerPlayer target) {
        return new SimpleMenuProvider((i, inventory, player) -> ChestMenu.fiveRows(i, target.getInventory()), INVENTORY_TITLE);
    }
}
