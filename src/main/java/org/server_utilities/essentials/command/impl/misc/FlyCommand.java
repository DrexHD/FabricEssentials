package org.server_utilities.essentials.command.impl.misc;

import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.CommandProperties;

public class FlyCommand extends SimpleToggleCommand {

    public FlyCommand() {
        super(CommandProperties.create("fly", 2), "fabric-essentials.commands.fly");
    }

    @Override
    protected boolean getState(ServerPlayer target) {
        return target.getAbilities().mayfly;
    }

    @Override
    protected void setState(ServerPlayer target, boolean state) {
        target.getAbilities().mayfly = state;
        if (!state) target.getAbilities().flying = false;
        target.onUpdateAbilities();
    }
}
