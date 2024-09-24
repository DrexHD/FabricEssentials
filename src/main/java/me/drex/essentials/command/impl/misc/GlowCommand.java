package me.drex.essentials.command.impl.misc;

import net.minecraft.server.level.ServerPlayer;
import me.drex.essentials.command.CommandProperties;

public class GlowCommand extends SimpleToggleCommand {

    public GlowCommand() {
        super(CommandProperties.create("glow", 2), "fabric-essentials.commands.glow");
    }

    @Override
    protected boolean getState(ServerPlayer target) {
        return target.hasGlowingTag();
    }

    @Override
    protected void setState(ServerPlayer target, boolean state) {
        target.setGlowingTag(state);
    }
}
