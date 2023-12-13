package org.server_utilities.essentials.command.impl.warp;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.storage.DataStorage;
import org.server_utilities.essentials.storage.ServerData;
import org.server_utilities.essentials.util.teleportation.Warp;

import java.util.HashMap;
import java.util.Map;

import static me.drex.message.api.LocalizedMessage.localized;

public class WarpsCommand extends Command {

    public WarpsCommand() {
        super(CommandProperties.create("warps", 0));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.executes(this::execute);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        Map<String, Warp> warps = DataStorage.serverData().getWarps();
        if (warps.isEmpty()) {
            ctx.getSource().sendFailure(localized("fabric-essentials.commands.warps.empty"));
        } else {
            Component warpsList = ComponentUtils.formatList(warps.entrySet(), localized("fabric-essentials.commands.warps.list.separator"), entry -> {
                return localized("fabric-essentials.commands.warps.list.element", entry.getValue().placeholders(entry.getKey()));
            });
            ctx.getSource().sendSystemMessage(localized("fabric-essentials.commands.warps", new HashMap<>(){{
                put("warp_list",warpsList);
            }}));
        }
        return warps.size();
    }
}
