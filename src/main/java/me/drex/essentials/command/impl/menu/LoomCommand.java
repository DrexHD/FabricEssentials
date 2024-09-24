package me.drex.essentials.command.impl.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.util.menu.DummyLoomMenu;

public class LoomCommand extends SimpleMenuCommand {

    private static final MutableComponent LOOM_TITLE = Component.translatable("container.loom");

    public LoomCommand() {
        super(CommandProperties.create("loom", 2));
    }

    @Override
    protected MenuProvider createMenu(ServerPlayer target) {
        return new SimpleMenuProvider((i, inventory, player) -> new DummyLoomMenu(i, inventory, ContainerLevelAccess.create(player.level(), player.blockPosition())), LOOM_TITLE);
    }
}
