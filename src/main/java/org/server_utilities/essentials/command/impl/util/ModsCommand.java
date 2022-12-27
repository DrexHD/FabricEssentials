package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.drex.message.api.Message;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.util.ComponentPlaceholderUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public class ModsCommand extends Command {

    public static final SimpleCommandExceptionType UNKNOWN = new SimpleCommandExceptionType(Message.message("fabric-essentials.commands.mods.mod.unknown"));
    private static final String MOD_ID = "modid";
    public static final String MODS_COMMAND = "mods";

    public ModsCommand() {
        super(Properties.create(MODS_COMMAND));
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
        Component modsList = ComponentUtils.formatList(mods, Message.message("fabric-essentials.commands.mods.list.separator"), mod -> {
            return Message.message("fabric-essentials.commands.mods.list.element", ComponentPlaceholderUtil.modPlaceholders(mod));
        });
        ctx.getSource().sendSystemMessage(Message.message("fabric-essentials.commands.mods", new HashMap<>() {{
            put("count", Component.literal(String.valueOf(mods.size())));
            put("mod_list", modsList);
        }}));
        return mods.size();
    }

    private int sendModInfo(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String modId = StringArgumentType.getString(ctx, MOD_ID);
        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer(modId);
        if (optional.isPresent()) {
            ctx.getSource().sendSystemMessage(Message.message("fabric-essentials.commands.mods.mod", ComponentPlaceholderUtil.modPlaceholders(optional.get())));
            return SUCCESS;
        } else {
            throw UNKNOWN.create();
        }
    }

    public static final SuggestionProvider<CommandSourceStack> MODS_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.suggest(FabricLoader.getInstance().getAllMods().stream().map(modContainer -> modContainer.getMetadata().getId()), builder);


}
