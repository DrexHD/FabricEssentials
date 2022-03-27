package org.server_utilities.essentials.command.menu;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.util.menu.DummyCraftingMenu;

public class WorkBenchCommand extends SimpleMenuCommand {

    private static final TranslatableComponent WORKBENCH_TITLE = new TranslatableComponent("container.crafting");

    public WorkBenchCommand() {
        super(Properties.create("wb", "workbench").permission("workbench"));
    }

    @Override
    protected MenuProvider createMenu(CommandContext<CommandSourceStack> ctx, ServerPlayer sender, ServerPlayer target) {
        return new SimpleMenuProvider((i, inventory, player) -> new DummyCraftingMenu(i, inventory, ContainerLevelAccess.create(player.level, player.blockPosition())), WORKBENCH_TITLE);
    }
}
