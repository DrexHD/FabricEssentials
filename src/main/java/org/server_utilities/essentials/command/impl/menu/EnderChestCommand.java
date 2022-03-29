package org.server_utilities.essentials.command.impl.menu;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import org.server_utilities.essentials.command.Properties;

public class EnderChestCommand extends SimpleMenuCommand {

    private static final TranslatableComponent ENDERCHEST_TITLE = new TranslatableComponent("container.enderchest");

    public EnderChestCommand() {
        super(Properties.create("ec", "enderchest").permission("enderchest"));
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, ServerPlayer sender, ServerPlayer target) {
        sendFeedback(ctx, "text.fabric-essentials.command.enderchest.other", target.getDisplayName(), ENDERCHEST_TITLE);
        sender.openMenu(createMenu(ctx, sender, target));
        return 1;
    }

    @Override
    protected MenuProvider createMenu(CommandContext<CommandSourceStack> ctx, ServerPlayer sender, ServerPlayer target) {
        return new SimpleMenuProvider((i, inventory, player) -> ChestMenu.threeRows(i, inventory, target.getEnderChestInventory()), ENDERCHEST_TITLE);
    }
}
