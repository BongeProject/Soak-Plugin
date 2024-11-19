package org.soak.wrapper.registry;

import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.Registry;

public interface ISoakRegistry<B extends Keyed> extends Registry<B> {

    RegistryKey<B> key();
}
