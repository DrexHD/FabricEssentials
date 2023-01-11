package org.server_utilities.essentials.command.impl.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.util.menu.DummyCraftingMenu;

public class WorkBenchCommand extends SimpleMenuCommand {

    private static final MutableComponent WORKBENCH_TITLE = Component.translatable("container.crafting");

    public WorkBenchCommand() {
        super(CommandProperties.create("workbench", new String[]{"craft", "wb"}, 2));
    }

    @Override
    protected MenuProvider createMenu(ServerPlayer target) {
        return new SimpleMenuProvider((i, inventory, player) -> new DummyCraftingMenu(i, inventory, ContainerLevelAccess.create(player.level, player.blockPosition())), WORKBENCH_TITLE);
    }
}
