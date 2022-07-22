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
    protected int onSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return setHat(ctx, ctx.getSource().getPlayerOrException(), true);
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        return setHat(ctx, target, false);
    }

    private int setHat(CommandContext<CommandSourceStack> ctx, ServerPlayer target, boolean self) {
        Inventory inventory = target.getInventory();
        ItemStack selected = inventory.getSelected();
        ResourceLocation resourceLocation = Registry.ITEM.getKey(selected.getItem());
        if (!permission(properties.getPermission(), "item", resourceLocation.getPath()).test(ctx.getSource())) {
            sendError(ctx,
                    "text.fabric-essentials.command.hat.no_permission");
            return -1;
        }
        ItemStack head = inventory.getArmor(EquipmentSlot.HEAD.getIndex());

        if (EnchantmentHelper.hasBindingCurse(head) && !permission(properties.getPermission(), "bypassBindingCurse").test(ctx.getSource())) {
            sendError(ctx,
                    "text.fabric-essentials.command.hat.binding_curse");
            return -2;
        }

        sendFeedback(ctx,
                String.format("text.fabric-essentials.command.hat.%s", self ? "self" : "other"),
                self ? new Object[]{resourceLocation.getPath()} : new Object[]{target.getDisplayName(), resourceLocation.getPath()}
        );
        if (!self) sendFeedback(target,
                String.format("text.fabric-essentials.command.hat.%s", "victim"),
                toName(ctx), resourceLocation.getPath()
        );
        target.setItemInHand(InteractionHand.MAIN_HAND, head);
        inventory.armor.set(EquipmentSlot.HEAD.getIndex(), selected);
        return 0;
    }
}
