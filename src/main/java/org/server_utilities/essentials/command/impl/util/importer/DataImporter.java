package org.server_utilities.essentials.command.impl.util.importer;

import net.minecraft.server.MinecraftServer;

public interface DataImporter {

    String getImporterId();

    boolean supports(DataType dataType);

    void importData(MinecraftServer server, DataType dataType);

    default void importData(MinecraftServer server) {
        for (DataType value : DataType.values()) {
            if (supports(value)) importData(server, value);
        }
    }

}
