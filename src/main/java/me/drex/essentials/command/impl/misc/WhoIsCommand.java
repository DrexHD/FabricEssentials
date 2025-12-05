package me.drex.essentials.command.impl.misc;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import eu.pb4.placeholders.api.PlaceholderContext;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;
import net.fabricmc.fabric.impl.event.interaction.FakePlayerNetworkHandler;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
//? if >= 1.21.9 {
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.level.storage.TagValueInput;
//?}
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
//? if >= 1.21.6 {
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.ValueInput;
//?}

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static me.drex.message.api.LocalizedMessage.localized;

public class WhoIsCommand extends Command {
    public WhoIsCommand() {
        super(CommandProperties.create("whois", 2));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
            Commands.argument("target", GameProfileArgument.gameProfile())
                .executes(ctx -> whois(ctx.getSource(), GameProfileArgument.getGameProfiles(ctx, "target")))
        );
    }

    private int whois(CommandSourceStack source, Collection</*? if >= 1.21.9 {*/ NameAndId /*?} else {*/ /*GameProfile *//*?}*/> targets) throws CommandSyntaxException {
        if (targets.isEmpty()) throw EntityArgument.NO_PLAYERS_FOUND.create();
        if (targets.size() > 1) throw EntityArgument.ERROR_NOT_SINGLE_PLAYER.create();
        var target = targets.iterator().next();
        MinecraftServer server = source.getServer();

        ServerPlayer serverPlayer = loadPlayer(server, target);
        source.sendSuccess(() -> localized("fabric-essentials.commands.whois", Collections.emptyMap(), PlaceholderContext.of(serverPlayer)), false);
        return 1;
    }

    private ServerPlayer loadPlayer(MinecraftServer server, /*? if >= 1.21.9 {*/ NameAndId /*?} else {*/ /*GameProfile *//*?}*/ target) {
        PlayerList playerList = server.getPlayerList();
        ServerPlayer player = playerList.getPlayer(/*? if >= 1.21.9 {*/ target.id()/*?} else {*/ /*target.getId() *//*?}*/);
        if (player != null) return player;

        ServerPlayer serverPlayer = new ServerPlayer(server, server.overworld(), /*? if >= 1.21.9 {*/ new GameProfile(target.id(), target.name()) /*?} else {*/ /*target *//*?}*/, ClientInformation.createDefault());
        new FakePlayerNetworkHandler(serverPlayer);
        //? if >= 1.21.6 {
        try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(serverPlayer.problemPath(), LOGGER)) {
            //? if >= 1.21.9 {
            Optional<ValueInput> optional = playerList.loadPlayerData(target)
                .map(compoundTag -> TagValueInput.create(scopedCollector, server.registryAccess(), compoundTag));
            optional.ifPresent(serverPlayer::load);
            //?} else {
            /*Optional<ValueInput> optional = playerList.load(serverPlayer, scopedCollector);
            ResourceKey<Level> resourceKey = optional.flatMap(valueInput -> valueInput.read("Dimension", Level.RESOURCE_KEY_CODEC))
                .orElse(Level.OVERWORLD);
            ServerLevel serverLevel = server.getLevel(resourceKey);
            if (serverLevel == null) {
                serverLevel = server.overworld();
            }
            serverPlayer.setServerLevel(serverLevel);
            *///?}
        }
        //?} else {
        /*Optional<CompoundTag> optional = playerList.load(serverPlayer);
        ResourceKey<Level> resourceKey = optional.flatMap(compoundTag -> DimensionType.parseLegacy(new Dynamic<>(NbtOps.INSTANCE, compoundTag.get("Dimension"))).resultOrPartial(LOGGER::error)).orElse(Level.OVERWORLD);
        ServerLevel serverLevel = server.getLevel(resourceKey);
        if (serverLevel == null) {
            serverLevel = server.overworld();
        }
        serverPlayer.setServerLevel(serverLevel);
        *///?}
        return serverPlayer;
    }
}
