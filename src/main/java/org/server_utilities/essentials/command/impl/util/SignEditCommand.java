package org.server_utilities.essentials.command.impl.util;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.server_utilities.essentials.command.Command;
import org.server_utilities.essentials.command.Properties;
import org.server_utilities.essentials.util.KeyUtil;
import org.server_utilities.essentials.util.StyledInputUtil;

public class SignEditCommand extends Command {

    public SignEditCommand() {
        super(Properties.create("signedit"));
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.then(
                Commands.argument("line", IntegerArgumentType.integer(1, 4))
                        .then(
                                Commands.argument("text", StringArgumentType.greedyString())
                                        .executes(ctx -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "line"), StringArgumentType.getString(ctx, "text")))
                        ).then(
                                Commands.literal("reset")
                                        .executes(ctx -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "line"), ""))
                        )
        );
    }

    protected int execute(CommandSourceStack src, int line, String text) throws CommandSyntaxException {
        Entity entity = src.getEntityOrException();
        HitResult hitResult = entity.pick(4.5f, 1, true);
        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockEntity blockEntity = src.getLevel().getBlockEntity(blockHitResult.getBlockPos());
            if (blockEntity instanceof SignBlockEntity signBlockEntity) {
                Component component = StyledInputUtil.parse(text, textTag -> KeyUtil.permission(src, "style.sign", textTag.name()));
                if (component.getString().length() > 45) {
                    sendFailure(src, "length");
                    return FAILURE;
                }
                sendSuccess(src, null, line, component);
                signBlockEntity.setMessage(line - 1, component);
                signBlockEntity.setChanged();
                src.getLevel().sendBlockUpdated(signBlockEntity.getBlockPos(), signBlockEntity.getBlockState(), signBlockEntity.getBlockState(), 3);
                return SUCCESS;
            }
        }
        sendFailure(src, "missing");
        return FAILURE;
    }

}
