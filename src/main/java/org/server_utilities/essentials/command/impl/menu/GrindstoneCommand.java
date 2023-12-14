package org.server_utilities.essentials.command.impl.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.util.menu.DummyGrindstoneMenu;

public class GrindstoneCommand extends SimpleMenuCommand {

    private static final MutableComponent GRINDSTONE_TITLE = Component.translatable("container.grindstone_title");

    public GrindstoneCommand() {
        super(CommandProperties.create("grindstone", 2));
    }

    @Override
    protected MenuProvider createMenu(ServerPlayer target) {
        return new SimpleMenuProvider((i, inventory, player) -> new DummyGrindstoneMenu(i, inventory, ContainerLevelAccess.create(player.level(), player.blockPosition())), GRINDSTONE_TITLE);
    }
}
