package org.server_utilities.essentials.command.impl.misc.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.drex.message.api.Message;
import me.drex.message.api.MessageAPI;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.time.StopWatch;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.command.impl.misc.admin.importer.DataImporter;
import org.server_utilities.essentials.command.impl.misc.admin.importer.KiloEssentialsImporter;
import org.server_utilities.essentials.config.ConfigManager;
import org.server_utilities.essentials.mixin.CommandSourceStackAccessor;
import org.server_utilities.essentials.util.ComponentPlaceholderUtil;
import org.spongepowered.configurate.ConfigurateException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class EssentialsCommand extends Command {

    public static final SimpleCommandExceptionType UNKNOWN = new SimpleCommandExceptionType(Message.message("fabric-essentials.commands.essentials.import.unknown"));
    private static final DataImporter[] DATA_IMPORTERS = new DataImporter[]{KiloEssentialsImporter.KILO_ESSENTIALS};

    public EssentialsCommand() {
        super(CommandProperties.create("essentials", 3));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.then(
                literal("reload")
                        .requires(require("reload"))
                        .executes(this::reload)

        ).then(
                literal("import")
                        .requires(require("import"))
                        .then(
                                argument("importer", string()).suggests(IMPORTER_PROVIDER)
                                        .executes(this::importData)
                        )
        );
    }

    private int reload(CommandContext<CommandSourceStack> ctx) {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            ConfigManager.INSTANCE.load();
            MessageAPI.reload();
            stopWatch.stop();
            ctx.getSource().sendSystemMessage(Message.message("fabric-essentials.commands.essentials.reload", new HashMap<>(){{
                put("time", Component.literal(String.valueOf(stopWatch.getTime())));
            }}));
            return SUCCESS;
        } catch (ConfigurateException configurateException) {
            LOGGER.error("An error occurred while loading the config, keeping old values", configurateException);
            ctx.getSource().sendSystemMessage(Message.message("fabric-essentials.commands.essentials.reload.error", ComponentPlaceholderUtil.exceptionPlaceholders(configurateException)));
            return FAILURE;
        }
    }

    private int importData(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!((CommandSourceStackAccessor) ctx.getSource()).getSource().equals(ctx.getSource().getServer())) {
            ctx.getSource().sendSystemMessage(Message.message("fabric-essentials.commands.essentials.import.console"));
            return FAILURE;
        }
        String importerId = getString(ctx, "importer");
        Optional<DataImporter> optional = Arrays.stream(DATA_IMPORTERS).filter(importer -> importer.getImporterId().equals(importerId)).findFirst();
        DataImporter dataImporter = optional.orElseThrow(UNKNOWN::create);
        dataImporter.importData(ctx.getSource().getServer());
        return SUCCESS;
    }

    public static final SuggestionProvider<CommandSourceStack> IMPORTER_PROVIDER = (ctx, builder) -> SharedSuggestionProvider.suggest(Arrays.stream(DATA_IMPORTERS).map(DataImporter::getImporterId), builder);


}
