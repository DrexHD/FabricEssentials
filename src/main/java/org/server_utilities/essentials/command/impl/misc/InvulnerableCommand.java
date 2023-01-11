package org.server_utilities.essentials.command.impl.misc;

import net.minecraft.server.level.ServerPlayer;
import org.server_utilities.essentials.command.CommandProperties;

public class InvulnerableCommand extends SimpleToggleCommand {

    public InvulnerableCommand() {
        super(CommandProperties.create("invulnerable", new String[]{"godmode"}, 2), "fabric-essentials.commands.invulnerable");
    }

    @Override
    protected boolean getState(ServerPlayer target) {
        return target.isInvulnerable();
    }

    @Override
    protected void setState(ServerPlayer target, boolean state) {
        target.setInvulnerable(state);
    }

}
