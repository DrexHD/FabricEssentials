package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.*;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;

import java.util.Collection;
import java.util.Optional;

public class ModsCommand extends Command {

    public static final SimpleCommandExceptionType DOESNT_EXIST = new SimpleCommandExceptionType(new TranslatableComponent("text.fabric-essentials.command.mods.doesnt_exist"));
    private static final String MOD_ID = "modid";
    public static final String MODS_COMMAND = "mods";

    public ModsCommand() {
        super(Properties.create(MODS_COMMAND).permission("mods"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        RequiredArgumentBuilder<CommandSourceStack, String> modId = Commands.argument(MOD_ID, StringArgumentType.string()).suggests(MODS_PROVIDER);
        modId.executes(this::sendModInfo);
        literal.then(modId);
        literal.executes(this::execute);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        Collection<ModContainer> mods = FabricLoader.getInstance().getAllMods();
        sendFeedback(ctx, "text.fabric-essentials.command.mods.list.title", mods.size());
        Component modsComponent = ComponentUtils.formatList(mods, this::formatMod);
        ctx.getSource().sendSuccess(modsComponent, false);
        return mods.size();
    }

    private int sendModInfo(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String modId = StringArgumentType.getString(ctx, MOD_ID);
        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer(modId);
        if (optional.isPresent()) {
            ModContainer modContainer = optional.get();
            ModMetadata metadata = modContainer.getMetadata();
            Component authors = ComponentUtils.formatList(metadata.getAuthors(), person -> new TextComponent(person.getName()));
            ctx.getSource().sendSuccess(new TextComponent(metadata.getName()), false);
            sendFeedback(ctx, "text.fabric-essentials.command.mods.info.version", metadata.getVersion().getFriendlyString());
            sendFeedback(ctx, "text.fabric-essentials.command.mods.info.author", authors);
            sendFeedback(ctx, "text.fabric-essentials.command.mods.info.description", metadata.getDescription());
            return 1;
        } else {
            throw DOESNT_EXIST.create();
        }
    }

    private Component formatMod(ModContainer modContainer) {
        return new TextComponent(modContainer.getMetadata().getId())
                .withStyle(
                        Style.EMPTY
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("text.fabric-essentials.command.mods.info.hover")))
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/%s %s", MODS_COMMAND, modContainer.getMetadata().getId())))
                );
    }

    public static final SuggestionProvider<CommandSourceStack> MODS_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.suggest(FabricLoader.getInstance().getAllMods().stream().map(modContainer -> modContainer.getMetadata().getId()), builder);


}
