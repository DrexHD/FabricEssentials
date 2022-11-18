package org.server_utilities.essentials.command.impl.menu;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.util.menu.DummyCraftingMenu;

public class WorkBenchCommand extends SimpleMenuCommand {

    private static final MutableComponent WORKBENCH_TITLE = Component.translatable("container.crafting");

    public WorkBenchCommand() {
        super(Properties.create("workbench", "craft"));
    }

    @Override
    protected MenuProvider createMenu(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        return new SimpleMenuProvider((i, inventory, player) -> new DummyCraftingMenu(i, inventory, ContainerLevelAccess.create(player.level, player.blockPosition())), WORKBENCH_TITLE);
    }
}
