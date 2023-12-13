package org.server_utilities.essentials.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.server_utilities.essentials.EssentialsMod;
import org.server_utilities.essentials.config.Config;
import org.server_utilities.essentials.config.ConfigManager;
import org.server_utilities.essentials.util.IdentifierUtil;
import org.slf4j.Logger;

import java.util.function.Predicate;

import static me.drex.message.api.LocalizedMessage.localized;

public abstract class Command {

    protected final CommandProperties defaultCommandProperties;
    protected static final Logger LOGGER = EssentialsMod.LOGGER;

    public Command(@NotNull CommandProperties commandProperties) {
        this.defaultCommandProperties = commandProperties;
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        CommandProperties commandProperties = config().commands.get(defaultCommandProperties.literal());
        if (commandProperties == null) commandProperties = defaultCommandProperties;
        String[] aliasLiterals = commandProperties.alias();
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(commandProperties.literal())
                .requires(require(null, commandProperties.defaultRequiredLevel()));
        registerArguments(builder, context);
        LiteralCommandNode<CommandSourceStack> root = dispatcher.register(builder);
        for (String aliasLiteral : aliasLiterals) {
            dispatcher.register(
                    Commands.literal(aliasLiteral)
                            .requires(builder.getRequirement())
                            .executes(builder.getCommand())
                            .redirect(root)
            );
        }
    }

    protected abstract void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext);

    public Predicate<CommandSourceStack> require(@Nullable String permission) {
        return require(permission, 2);
    }

    public Predicate<CommandSourceStack> require(@Nullable String permission, int defaultRequiredLevel) {
        return src -> {
            try {
                return Permissions.check(src, permission(permission), defaultRequiredLevel);
            } catch (Throwable ignored) {
                // Fallback for datapack compatibility
                return src.hasPermission(defaultRequiredLevel);
            }
        };
    }

    public Predicate<CommandSourceStack> require(@Nullable String permission, boolean fallback) {
        return src -> {
            try {
                return Permissions.check(src, permission(permission), fallback);
            } catch (Throwable ignored) {
                // Fallback for datapack compatibility
                return fallback;
            }
        };
    }

    public boolean check(CommandSourceStack src, String permission) {
        return require(permission).test(src);
    }

    public boolean check(CommandSourceStack src, String permission, int defaultRequiredLevel) {
        return require(permission, defaultRequiredLevel).test(src);
    }

    public boolean check(CommandSourceStack src, String permission, boolean fallback) {
        return require(permission, fallback).test(src);
    }

    public String permission(@Nullable String permission) {
        if (permission == null) {
            return IdentifierUtil.permission("command." + defaultCommandProperties.literal());
        } else {
            return IdentifierUtil.permission("command." + defaultCommandProperties.literal() + "." + permission);
        }
    }

    public CommandProperties defaultProperties() {
        return this.defaultCommandProperties;
    }

    public static Config config() {
        return ConfigManager.config();
    }

    public static final SimpleCommandExceptionType WORLD_UNKNOWN = new SimpleCommandExceptionType(localized("fabric-essentials.location.world.unknown"));
    public static final int SUCCESS = 1;
    public static final int FAILURE = 0;

}
