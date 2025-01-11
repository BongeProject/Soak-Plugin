package org.soak.wrapper.v1_21_R2;

import org.soak.annotation.UsesNms;
import org.soak.exception.NMSUsageException;
import org.soak.wrapper.SoakServer;
import org.spongepowered.api.Server;

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
