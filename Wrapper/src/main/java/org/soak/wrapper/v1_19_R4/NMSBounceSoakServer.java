package org.soak.wrapper.v1_19_R4;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.annotation.UsesNms;
import org.soak.exception.NMSUsageException;
import org.soak.wrapper.SoakServer;
import org.spongepowered.api.Server;

import java.util.UUID;
import java.util.function.Supplier;

public class NMSBounceSoakServer extends SoakServer {

    public NMSBounceSoakServer(Supplier<Server> serverSupplier) {
        super(serverSupplier);
    }

    @UsesNms()
    public Object getHandle() {
        //returns dedicatedPlayerList -> Maybe able to fake it for those that use only reflection and dont check the name
        throw new NMSUsageException(org.bukkit.Server.class.getSimpleName(), "getHandle");
    }

}
