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

// TODO: Add translations
public class GlowCommand extends OptionalOnlineTargetCommand {

    public static final String MESSAGE_ID = "text.fabric-essentials.command.glow.%s";

    public GlowCommand() {
        super(Properties.create("glow").permission("glow"));
    }

    @Override
    protected int onSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return setGlowing(ctx, ctx.getSource().getPlayerOrException(), true);
    }

    @Override
    protected int onOther(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        return setGlowing(ctx, target, false);
    }

    private int setGlowing(CommandContext<CommandSourceStack> ctx, ServerPlayer target, boolean self) {
        sendFeedback(ctx,
                MESSAGE_ID.formatted(self ? "self" : "other"),
                self ? new Object[]{} : new Object[]{target.getDisplayName()}
        );
        boolean prev = target.hasGlowingTag();
        if (!self) sendFeedback(target,
                MESSAGE_ID.formatted("victim"),
                toName(ctx)
        );
        target.setGlowingTag(!prev);
        return 0;
    }
}
