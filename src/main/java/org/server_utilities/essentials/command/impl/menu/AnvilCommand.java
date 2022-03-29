package org.server_utilities.essentials.command.impl.menu;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.util.menu.DummyAnvilMenu;

public class AnvilCommand extends SimpleMenuCommand {

    private static final TranslatableComponent ANVIL_TITLE = new TranslatableComponent("container.repair");

    public AnvilCommand() {
        super(Properties.create("anvil").permission("anvil"));
    }

    @Override
    protected MenuProvider createMenu(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        return new SimpleMenuProvider((i, inventory, player) -> new DummyAnvilMenu(i, inventory, ContainerLevelAccess.create(player.level, player.blockPosition())), ANVIL_TITLE);
    }
}
