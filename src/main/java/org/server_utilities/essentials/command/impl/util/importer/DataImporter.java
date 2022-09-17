package org.server_utilities.essentials.command.impl.util.importer;

import net.minecraft.server.MinecraftServer;

public interface DataImporter {

    String getImporterId();

    void importData(MinecraftServer server);

}
