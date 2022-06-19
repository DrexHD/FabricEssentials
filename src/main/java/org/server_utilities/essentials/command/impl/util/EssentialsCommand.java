package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.time.StopWatch;
import org.server_utilities.essentials.EssentialsMod;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;
import org.spongepowered.configurate.ConfigurateException;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class EssentialsCommand extends Command {

    public EssentialsCommand() {
        super(Properties.create("essentials"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        LiteralArgumentBuilder<CommandSourceStack> reload = LiteralArgumentBuilder.literal("reload");
        reload.executes(ctx -> execute(ctx.getSource()));
        literal.then(reload);
    }

    private int execute(CommandSourceStack source) throws CommandSyntaxException {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            EssentialsMod.getConfig().load();
            stopWatch.stop();
            sendFeedback(source, "text.fabric-essentials.command.reload", stopWatch.getTime());
            return 1;
        } catch (ConfigurateException e) {
            LOGGER.error("An error occurred while loading the config, keeping old values", e);
            throw new SimpleCommandExceptionType(Component.translatable("text.fabric-essentials.command.reload.error", e.getMessage())).create();
        }
    }
}
