package org.server_utilities.essentials.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;

import static me.drex.message.api.LocalizedMessage.localized;

public class HatCommand extends Command {

    public HatCommand() {
        super(CommandProperties.create("hat", 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.executes(this::setHat);
    }

    protected int setHat(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Inventory inventory = player.getInventory();
        ItemStack selected = inventory.getSelected();
        ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey(selected.getItem());
        if (!check(ctx.getSource(), "item." + resourceLocation.getPath())) {
            ctx.getSource().sendFailure(localized("fabric-essentials.commands.hat.no_permission"));
            return FAILURE;
        }
        ItemStack head = inventory.getArmor(EquipmentSlot.HEAD.getIndex());

        if (EnchantmentHelper.has(head, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) && !check(ctx.getSource(), "bypassBindingCurse")) {
            ctx.getSource().sendFailure(localized("fabric-essentials.commands.hat.binding_curse"));
            return FAILURE;
        }

        ctx.getSource().sendSuccess(() -> localized("fabric-essentials.commands.hat"), false);
        player.setItemInHand(InteractionHand.MAIN_HAND, head);
        inventory.armor.set(EquipmentSlot.HEAD.getIndex(), selected);
        return SUCCESS;
    }
}
