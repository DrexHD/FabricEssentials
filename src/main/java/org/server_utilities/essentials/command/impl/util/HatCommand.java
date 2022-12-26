package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.drex.message.api.Message;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;

public class HatCommand extends Command {

    public HatCommand() {
        super(Properties.create("hat"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.executes(this::execute);
    }

    protected int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Inventory inventory = player.getInventory();
        ItemStack selected = inventory.getSelected();
        ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey(selected.getItem());
        if (!predicate("item", resourceLocation.getPath()).test(ctx.getSource())) {
            ctx.getSource().sendFailure(Message.message("fabric-essentials.commands.hat.no_permission"));
            return FAILURE;
        }
        ItemStack head = inventory.getArmor(EquipmentSlot.HEAD.getIndex());

        if (EnchantmentHelper.hasBindingCurse(head) && !predicate("bypassBindingCurse").test(ctx.getSource())) {
            ctx.getSource().sendFailure(Message.message("fabric-essentials.commands.hat.binding_curse"));
            return FAILURE;
        }

        ctx.getSource().sendSuccess(Message.message("fabric-essentials.commands.hat"), false);
        player.setItemInHand(InteractionHand.MAIN_HAND, head);
        inventory.armor.set(EquipmentSlot.HEAD.getIndex(), selected);
        return SUCCESS;
    }
}
