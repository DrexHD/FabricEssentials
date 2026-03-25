package me.drex.essentials.command.impl.misc;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;
import net.fabricmc.fabric.impl.event.interaction.FakePlayerPacketListener;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.ValueInput;

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

    private int whois(CommandSourceStack source, Collection<NameAndId> targets) throws CommandSyntaxException {
        if (targets.isEmpty()) throw EntityArgument.NO_PLAYERS_FOUND.create();
        if (targets.size() > 1) throw EntityArgument.ERROR_NOT_SINGLE_PLAYER.create();
        var target = targets.iterator().next();
        MinecraftServer server = source.getServer();

        ServerPlayer serverPlayer = loadPlayer(server, target);
        source.sendSuccess(() -> localized("fabric-essentials.commands.whois", Collections.emptyMap(), ServerPlaceholderContext.of(serverPlayer)), false);
        return 1;
    }

    private ServerPlayer loadPlayer(MinecraftServer server, NameAndId target) {
        PlayerList playerList = server.getPlayerList();
        ServerPlayer player = playerList.getPlayer(target.id());
        if (player != null) return player;

        ServerPlayer serverPlayer = new ServerPlayer(server, server.overworld(), new GameProfile(target.id(), target.name()), ClientInformation.createDefault());
        new FakePlayerPacketListener(serverPlayer);
        try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(serverPlayer.problemPath(), LOGGER)) {
            Optional<ValueInput> optional = playerList.loadPlayerData(target)
                .map(compoundTag -> TagValueInput.create(scopedCollector, server.registryAccess(), compoundTag));
            optional.ifPresent(serverPlayer::load);
        }
        return serverPlayer;
    }
}
