package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.command.util.OptionalOnlineTargetCommand;

public class HatCommand extends OptionalOnlineTargetCommand {

    public HatCommand() {
        super(Properties.create("hat").permission("hat"));
    }

    @Override
    protected int execute(CommandContext<CommandSourceStack> ctx, ServerPlayer target, boolean self) throws CommandSyntaxException {
        Inventory inventory = target.getInventory();
        ItemStack selected = inventory.getSelected();
        ResourceLocation resourceLocation = Registry.ITEM.getKey(selected.getItem());
        if (!predicate("item", resourceLocation.getPath()).test(ctx.getSource())) {
            sendFailure(ctx.getSource(),
                    "no_permission");
            return FAILURE;
        }
        ItemStack head = inventory.getArmor(EquipmentSlot.HEAD.getIndex());

        if (EnchantmentHelper.hasBindingCurse(head) && !predicate("bypassBindingCurse").test(ctx.getSource())) {
            sendFailure(ctx.getSource(),
                    "binding_curse");
            return FAILURE;
        }

        sendFeedbackWithOptionalTarget(ctx, target, self, new Object[]{resourceLocation.getPath()}, new Object[]{target.getDisplayName(), resourceLocation.getPath()}, new Object[]{ctx.getSource().getDisplayName(), resourceLocation.getPath()});
        target.setItemInHand(InteractionHand.MAIN_HAND, head);
        inventory.armor.set(EquipmentSlot.HEAD.getIndex(), selected);
        return SUCCESS;
    }

}
