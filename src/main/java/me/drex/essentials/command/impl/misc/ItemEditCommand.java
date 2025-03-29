package me.drex.essentials.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.config.ItemEditConfig;
import me.drex.essentials.util.StyledInputUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

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
                    argument("line", integer(1, config().itemEdit.lore.maxLines))
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
        ItemEditConfig.NameConfig nameConfig = config().itemEdit.name;
        MutableComponent component;
        Component parsed = StyledInputUtil.parse(name, src, "style.item.name.");
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
                itemStack.set(DataComponents.CUSTOM_NAME, null);
            } else {
                itemStack.set(DataComponents.CUSTOM_NAME, component.withStyle(Style.EMPTY.withItalic(false)));
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
        ItemEditConfig.LoreConfig loreConfig = config().itemEdit.lore;
        MutableComponent component;
        Component parsed = StyledInputUtil.parse(lore, src, "style.item.lore.");
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
            AtomicInteger result = new AtomicInteger();
            itemStack.update(DataComponents.LORE, ItemLore.EMPTY, itemLore -> {
                List<Component> lines = new LinkedList<>(itemLore.lines());
                if (clear) {
                    if (line <= lines.size()) {
                        lines.remove(line - 1);
                    } else {
                        src.sendFailure(localized("fabric-essentials.commands.itemedit.lore.invalid_line"));
                        result.set(FAILURE);
                        return itemLore;
                    }
                } else {
                    for (int i = itemLore.lines().size(); i <= line - 1; i++) {
                        lines.add(Component.empty());
                    }
                    lines.set(line - 1, component.withStyle(Style.EMPTY.withItalic(false).withColor(ChatFormatting.WHITE)));
                }
                src.sendSuccess(() -> localized("fabric-essentials.commands.itemedit.lore", Map.of(
                    "line", Component.literal(String.valueOf(line)),
                    "lore", component)), false);
                result.set(SUCCESS);
                return new ItemLore(lines);
            });

            return result.get();
        } else {
            src.sendFailure(localized("fabric-essentials.commands.itemedit.experience"));
            return FAILURE;
        }
    }

}
