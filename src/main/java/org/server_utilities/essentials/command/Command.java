package org.server_utilities.essentials.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.server_utilities.essentials.EssentialsMod;
import org.server_utilities.essentials.config.ConfigManager;
import org.server_utilities.essentials.config.EssentialsConfig;
import org.server_utilities.essentials.util.KeyUtil;
import org.slf4j.Logger;

import java.util.function.Predicate;

public abstract class Command {

    protected final Properties properties;
    protected static final Logger LOGGER = EssentialsMod.LOGGER;
    protected static final Object[] EMPTY = new Object[]{};

    public Command(@NotNull Properties properties) {
        this.properties = properties;
    }

    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        String[] alias = this.properties.alias();
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(properties.literal()).requires(predicate());
        register(builder);
        LiteralCommandNode<CommandSourceStack> root = dispatcher.register(builder);
        for (String s : alias) {
            dispatcher.register(Commands.literal(s).requires(predicate()).redirect(root));
        }
    }

    protected abstract void register(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder);

    public Predicate<CommandSourceStack> predicate(String... permission) {
        return commandSourceStack -> Permissions.check(commandSourceStack, permission(permission), 2);
    }

    public String permission(String... permission) {
        return KeyUtil.permission("command", properties.literal(), join(permission));
    }

    public String translation(String... keys) {
        return KeyUtil.translation("command", properties.literal(), join(keys));
    }

    public String join(String... parts) {
        return KeyUtil.join(parts);
    }

    public static EssentialsConfig config() {
        return ConfigManager.INSTANCE.config();
    }

    public void sendSuccess(CommandSourceStack src, String subKey, Object... args) {
        src.sendSuccess(Component.translatable(translation(subKey), args), false);
    }

    public void sendFailure(CommandSourceStack src, String subKey, Object... args) {
        src.sendFailure(Component.translatable(translation(subKey), args));
    }

    public static final SimpleCommandExceptionType WORLD_UNKNOWN = new SimpleCommandExceptionType(Component.translatable("text.fabric-essentials.location.world.unknown"));
    public static final int SUCCESS = 1;
    public static final int FAILURE = 0;

}
