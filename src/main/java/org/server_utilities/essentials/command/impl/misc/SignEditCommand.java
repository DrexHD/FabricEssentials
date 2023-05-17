package org.server_utilities.essentials.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.common.protection.api.CommonProtection;
import me.drex.message.api.Message;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.CommandProperties;
import org.server_utilities.essentials.util.IdentifierUtil;
import org.server_utilities.essentials.util.StyledInputUtil;

import java.util.HashMap;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SignEditCommand extends Command {

    public SignEditCommand() {
        super(CommandProperties.create("signedit", 0));
    }

    @Override
    protected void registerArguments(LiteralArgumentBuilder<CommandSourceStack> literal, CommandBuildContext commandBuildContext) {
        literal.then(
                argument("line", integer(1, 4))
                        .then(
                                argument("text", greedyString())
                                        .executes(ctx -> editSignText(ctx.getSource(), getInteger(ctx, "line"), getString(ctx, "text")))
                        ).then(
                                literal("clear")
                                        .executes(ctx -> editSignText(ctx.getSource(), getInteger(ctx, "line"), ""))
                        )
        );
    }

    protected int editSignText(CommandSourceStack src, int line, String text) throws CommandSyntaxException {
        ServerPlayer player = src.getPlayerOrException();
        HitResult hitResult = player.pick(4.5f, 1, true);
        if (hitResult instanceof BlockHitResult blockHitResult) {
            if (CommonProtection.canBreakBlock(src.getLevel(), blockHitResult.getBlockPos(), player.getGameProfile(), player) &&
                    CommonProtection.canPlaceBlock(src.getLevel(), blockHitResult.getBlockPos(), player.getGameProfile(), player)) {
                BlockEntity blockEntity = src.getLevel().getBlockEntity(blockHitResult.getBlockPos());
                if (blockEntity instanceof SignBlockEntity signBlockEntity) {
                    Component component = StyledInputUtil.parse(text, textTag -> IdentifierUtil.check(src, "style.sign." + textTag.name()));
                    if (component.getString().length() > 45) {
                        src.sendFailure(Message.message("fabric-essentials.commands.signedit.length"));
                        return FAILURE;
                    }
                    src.sendSuccess(() -> Message.message("fabric-essentials.commands.signedit", new HashMap<>() {{
                        put("sign_line", Component.literal(String.valueOf(line)));
                        put("sign_text", component);
                    }}), false);
                    signBlockEntity.updateText(signText -> signText.setMessage(line - 1, component), signBlockEntity.isFacingFrontText(player));
                    signBlockEntity.setChanged();
                    src.getLevel().sendBlockUpdated(signBlockEntity.getBlockPos(), signBlockEntity.getBlockState(), signBlockEntity.getBlockState(), 3);
                    return SUCCESS;
                }
            } else {
                src.sendFailure(Message.message("fabric-essentials.commands.signedit.cant_modify"));
                return FAILURE;
            }

        }
        src.sendFailure(Message.message("fabric-essentials.commands.signedit.missing"));
        return FAILURE;
    }

}
