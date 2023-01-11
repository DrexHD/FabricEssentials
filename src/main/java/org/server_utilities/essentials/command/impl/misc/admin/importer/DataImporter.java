package org.server_utilities.essentials.command.impl.misc.admin.importer;

import net.minecraft.server.MinecraftServer;

public interface DataImporter {

    String getImporterId();

    void importData(MinecraftServer server);

}
