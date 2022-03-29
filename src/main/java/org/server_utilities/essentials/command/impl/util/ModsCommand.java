package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;

import java.util.Collection;

public class ModsCommand extends Command {

    public ModsCommand() {
        super(Properties.create("mods").permission("mods"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.executes(this::execute);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        Collection<ModContainer> mods = FabricLoader.getInstance().getAllMods();
        ctx.getSource().sendSuccess(new TranslatableComponent("text.fabric-essentials.command.mods.list.title", mods.size()), false);
        String joinedMods = String.join(", ", mods.stream().map(modContainer -> modContainer.getMetadata().getName()).toList());
        ctx.getSource().sendSuccess(new TextComponent(joinedMods), false);
        return mods.size();
    }

}
