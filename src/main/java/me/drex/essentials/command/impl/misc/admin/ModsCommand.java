package me.drex.essentials.command.impl.misc.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.util.ComponentPlaceholderUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;

public class ModsCommand extends Command {

    public static final SimpleCommandExceptionType UNKNOWN = new SimpleCommandExceptionType(localized("fabric-essentials.commands.mods.mod.unknown"));

    public ModsCommand() {
        super(CommandProperties.create("mods", 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
                argument("mod", string()).suggests(MODS_PROVIDER)
                        .executes(this::sendModInfo)
        ).executes(this::sendModList);

    }

    private int sendModList(CommandContext<CommandSourceStack> ctx) {
        Collection<ModContainer> mods = FabricLoader.getInstance().getAllMods().stream()
            .filter(modContainer -> modContainer.getOrigin().getKind() == ModOrigin.Kind.PATH)
            .toList();
        Component modsList = ComponentUtils.formatList(mods, localized("fabric-essentials.commands.mods.list.separator"), mod -> {
            return localized("fabric-essentials.commands.mods.list.element", ComponentPlaceholderUtil.modPlaceholders(mod));
        });
        ctx.getSource().sendSystemMessage(localized("fabric-essentials.commands.mods", new HashMap<>() {{
            put("mod_count", Component.literal(String.valueOf(mods.size())));
            put("mod_list", modsList);
        }}));
        return mods.size();
    }

    private int sendModInfo(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String modId = getString(ctx, "mod");
        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer(modId);
        if (optional.isPresent()) {
            ctx.getSource().sendSystemMessage(localized("fabric-essentials.commands.mods.mod", ComponentPlaceholderUtil.modPlaceholders(optional.get())));
            return SUCCESS;
        } else {
            throw UNKNOWN.create();
        }
    }

    public static final SuggestionProvider<CommandSourceStack> MODS_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.suggest(FabricLoader.getInstance().getAllMods().stream().map(modContainer -> modContainer.getMetadata().getId()), builder);


}
