package me.drex.essentials.command.impl.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.util.menu.DummyCartographyTableMenu;

public class CartographyTableCommand extends SimpleMenuCommand {

    private static final MutableComponent CARTOGRAPHY_TABLE_TITLE = Component.translatable("container.cartography_table");

    public CartographyTableCommand() {
        super(CommandProperties.create("cartographytable", 2));
    }

    @Override
    protected MenuProvider createMenu(ServerPlayer target) {
        return new SimpleMenuProvider((i, inventory, player) -> new DummyCartographyTableMenu(i, inventory, ContainerLevelAccess.create(player.level(), player.blockPosition())), CARTOGRAPHY_TABLE_TITLE);
    }
}
