package me.drex.essentials.command.impl.menu;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.util.menu.DummyEnchantmentMenu;

public class EnchantmentCommand extends SimpleMenuCommand {

    private static final MutableComponent ENCHANT_TITLE = Component.translatable("container.enchant");

    public EnchantmentCommand() {
        super(CommandProperties.create("enchantment", 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        super.registerArguments(literal, commandBuildContext);
        literal.then(
            Commands.argument("bookcases", IntegerArgumentType.integer(0, 15))
                .requires(require("bookcases"))
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    player.openMenu(createMenu(player, IntegerArgumentType.getInteger(ctx, "bookcases")));
                    return SUCCESS;
                })
        );
    }

    @Override
    protected MenuProvider createMenu(ServerPlayer target) {
        return createMenu(target, 15);
    }

    protected MenuProvider createMenu(ServerPlayer target, int bookcases) {
        return new SimpleMenuProvider((i, inventory, player) -> new DummyEnchantmentMenu(bookcases, i, inventory, ContainerLevelAccess.create(player.level(), player.blockPosition())), ENCHANT_TITLE);
    }
}
