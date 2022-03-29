package org.server_utilities.essentials.command.impl.menu;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.util.menu.LinkedInventory;

public class InventoryCommand extends SimpleMenuCommand {

    private static final TranslatableComponent INVENTORY_TITLE = new TranslatableComponent("container.inventory");

    public InventoryCommand() {
        super(Properties.create("inv", "inventory").permission("inventory"));
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, ServerPlayer sender, ServerPlayer target) {
        sendFeedback(ctx, "text.fabric-essentials.command.inventory.other", target.getDisplayName(), INVENTORY_TITLE);
        sender.openMenu(createMenu(ctx, sender, target));
        return 1;
    }

    @Override
    protected MenuProvider createMenu(CommandContext<CommandSourceStack> ctx, ServerPlayer sender, ServerPlayer target) {
        return new SimpleMenuProvider((syncId, inventory, player) -> new ChestMenu(MenuType.GENERIC_9x5, syncId, sender.getInventory(), new LinkedInventory(target), 5), INVENTORY_TITLE);
    }
}
