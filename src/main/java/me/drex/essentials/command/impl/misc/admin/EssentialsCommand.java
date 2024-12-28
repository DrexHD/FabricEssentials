package me.drex.essentials.command.impl.misc.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.drex.essentials.command.impl.misc.admin.importer.EssentialCommandsImporter;
import me.drex.message.api.MessageAPI;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.time.StopWatch;
import me.drex.essentials.command.Command;
import me.drex.essentials.command.CommandProperties;
import me.drex.essentials.command.impl.misc.admin.importer.DataImporter;
import me.drex.essentials.command.impl.misc.admin.importer.KiloEssentialsImporter;
import me.drex.essentials.config.ConfigManager;
import me.drex.essentials.mixin.CommandSourceStackAccessor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static me.drex.message.api.LocalizedMessage.localized;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class EssentialsCommand extends Command {

    public static final SimpleCommandExceptionType UNKNOWN = new SimpleCommandExceptionType(localized("fabric-essentials.commands.essentials.import.unknown"));
    private static final DataImporter[] DATA_IMPORTERS = new DataImporter[]{KiloEssentialsImporter.KILO_ESSENTIALS, EssentialCommandsImporter.ESSENTIAL_COMMANDS};

    public EssentialsCommand() {
        super(CommandProperties.create("essentials", 3));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
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
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        if (!ConfigManager.load()) {
            ctx.getSource().sendSystemMessage(localized("fabric-essentials.commands.essentials.reload.error"));
            return FAILURE;
        }
        MessageAPI.reload();
        stopWatch.stop();
        ctx.getSource().sendSystemMessage(localized("fabric-essentials.commands.essentials.reload", new HashMap<>(){{
            put("time", Component.literal(String.valueOf(stopWatch.getTime())));
        }}));
        return SUCCESS;
    }

    private int importData(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!((CommandSourceStackAccessor) ctx.getSource()).getSource().equals(ctx.getSource().getServer())) {
            ctx.getSource().sendSystemMessage(localized("fabric-essentials.commands.essentials.import.console"));
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
