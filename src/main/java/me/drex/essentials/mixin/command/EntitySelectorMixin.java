package me.drex.essentials.mixin.command;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.drex.essentials.config.ConfigManager;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(EntitySelector.class)
public abstract class EntitySelectorMixin {

    @WrapOperation(
        method = "findPlayers",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/players/PlayerList;getPlayerByName(Ljava/lang/String;)Lnet/minecraft/server/level/ServerPlayer;"
        )
    )
    public ServerPlayer allowPartialName(PlayerList playerList, String input, Operation<ServerPlayer> original) throws CommandSyntaxException {
        if (ConfigManager.config().commands.allowPartialNames) {
            String lowercasePartial = input.toLowerCase();
            List<ServerPlayer> matches = playerList.getPlayers().stream()
                .filter(player -> player.getGameProfile().getName().toLowerCase().startsWith(lowercasePartial))
                .toList();

            if (matches.isEmpty()) {
                throw new SimpleCommandExceptionType(
                    Component.literal("No player found matching '" + input + "'"))
                    .create();
            }
            if (matches.size() > 1) {
                throw new SimpleCommandExceptionType(
                    Component.literal("Multiple players found matching '" + input + "'"))
                    .create();
            }
            return matches.getFirst();
        } else {
            return original.call(playerList, input);
        }
    }

}
