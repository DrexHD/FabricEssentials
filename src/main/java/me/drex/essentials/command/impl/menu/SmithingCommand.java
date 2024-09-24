package me.drex.essentials.command.impl.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.util.menu.DummySmithingMenu;

public class SmithingCommand extends SimpleMenuCommand {

    private static final MutableComponent SMITHING_TITLE = Component.translatable("container.upgrade");

    public SmithingCommand() {
        super(CommandProperties.create("smithing", 2));
    }

    @Override
    protected MenuProvider createMenu(ServerPlayer target) {
        return new SimpleMenuProvider((i, inventory, player) -> new DummySmithingMenu(i, inventory, ContainerLevelAccess.create(player.level(), player.blockPosition())), SMITHING_TITLE);
    }
}
