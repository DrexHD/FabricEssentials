package org.server_utilities.essentials.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.config.commands.ItemEditConfig;
import org.server_utilities.essentials.util.IdentifierUtil;
import org.server_utilities.essentials.util.StyledInputUtil;

import java.util.Map;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.world.item.ItemStack.TAG_DISPLAY;
import static net.minecraft.world.item.ItemStack.TAG_LORE;

public class ItemEditCommand extends Command {

    public ItemEditCommand() {
        super(CommandProperties.create("itemedit", 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
            literal("name")
                .requires(require("name"))
                .then(
                    argument("name", greedyString()).executes(context -> editItemName(context.getSource(), getString(context, "name"), false))
                ).then(
                    literal("clear").executes(context -> editItemName(context.getSource(), "", true))
                )
        ).then(
            literal("lore")
                .requires(require("lore"))
                .then(
                    argument("line", integer(1, config().commands.itemEdit.lore.maxLines))
                        .then(
                            argument("lore", greedyString()).executes(context -> editItemLore(context.getSource(), getInteger(context, "line"), getString(context, "lore"), false))
                        ).then(
                            literal("clear").executes(context -> editItemLore(context.getSource(), getInteger(context, "line"), "", true))
                        )
                )
        );
    }

    protected int editItemName(CommandSourceStack src, String name, boolean clear) throws CommandSyntaxException {
        ServerPlayer player = src.getPlayerOrException();
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.isEmpty()) {
            src.sendFailure(localized("fabric-essentials.commands.itemedit.missing"));
            return FAILURE;
        }
        ItemEditConfig.NameConfig nameConfig = config().commands.itemEdit.name;
        MutableComponent component;
        Component parsed = StyledInputUtil.parse(name, textTag -> IdentifierUtil.check(src, "style.item.name." + textTag.name()));
        if (parsed.getString().equals(name)) {
            component = Component.literal(name);
        } else {
            component = (MutableComponent) parsed;
        }
        if (parsed.getString().length() > nameConfig.maxLength) {
            src.sendFailure(localized("fabric-essentials.commands.itemedit.name.length"));
            return FAILURE;
        }

        if (player.experienceLevel >= nameConfig.experienceLevelCost || player.isCreative()) {
            if (!player.isCreative()) player.giveExperienceLevels(-nameConfig.experienceLevelCost);
            if (clear) {
                itemStack.resetHoverName();
            } else {
                itemStack.setHoverName(component.withStyle(Style.EMPTY.withItalic(false)));
            }
            src.sendSuccess(() -> localized("fabric-essentials.commands.itemedit.name", Map.of(
                "name", itemStack.getHoverName())), false);
            return SUCCESS;
        } else {
            src.sendFailure(localized("fabric-essentials.commands.itemedit.experience"));
            return FAILURE;
        }
    }

    protected int editItemLore(CommandSourceStack src, int line, String lore, boolean clear) throws CommandSyntaxException {
        ServerPlayer player = src.getPlayerOrException();
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.isEmpty()) {
            src.sendFailure(localized("fabric-essentials.commands.itemedit.missing"));
            return FAILURE;
        }
        ItemEditConfig.LoreConfig loreConfig = config().commands.itemEdit.lore;
        MutableComponent component;
        Component parsed = StyledInputUtil.parse(lore, textTag -> IdentifierUtil.check(src, "style.item.lore." + textTag.name()));
        if (parsed.getString().equals(lore)) {
            component = Component.literal(lore);
        } else {
            component = (MutableComponent) parsed;
        }
        if (parsed.getString().length() > loreConfig.maxLength) {
            src.sendFailure(localized("fabric-essentials.commands.itemedit.lore.length"));
            return FAILURE;
        }

        if (player.experienceLevel >= loreConfig.experienceLevelCost || player.isCreative()) {
            if (!player.isCreative()) player.giveExperienceLevels(-loreConfig.experienceLevelCost);
            CompoundTag displayTag = itemStack.getOrCreateTagElement(TAG_DISPLAY);
            if (!displayTag.contains(TAG_LORE)) {
                displayTag.put(TAG_LORE, new ListTag());
            }
            ListTag loreTag = displayTag.getList(TAG_LORE, Tag.TAG_STRING);
            if (clear) {
                if (line <= loreTag.size()) {
                    loreTag.remove(line - 1);
                } else {
                    src.sendFailure(localized("fabric-essentials.commands.itemedit.lore.invalid_line"));
                    return FAILURE;
                }
            } else {
                for (int i = loreTag.size(); i <= line - 1; i++) {
                    loreTag.add(StringTag.valueOf(Component.Serializer.toJson(Component.empty())));
                }
                loreTag.set(line - 1, StringTag.valueOf(Component.Serializer.toJson(component.withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.WHITE)))));
            }
            src.sendSuccess(() -> localized("fabric-essentials.commands.itemedit.lore", Map.of(
                "line", Component.literal(String.valueOf(line)),
                "lore", component)), false);
            return SUCCESS;
        } else {
            src.sendFailure(localized("fabric-essentials.commands.itemedit.experience"));
            return FAILURE;
        }
    }

}
