package org.soak.wrapper.registry;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.jspecify.annotations.Nullable;
import org.soak.exception.NotImplementedException;

@SuppressWarnings("NonExtendableApiUsage")
public class SoakRegistryAccess implements RegistryAccess {
    @Override
    public @Nullable <T extends Keyed> Registry<T> getRegistry(Class<T> aClass) {
        return switch (aClass.getSimpleName()) {
            case "Potion" -> (Registry<T>) getRegistry(RegistryKey.POTION);
            default -> throw new IllegalArgumentException("Unknown registry for class: " + aClass.getSimpleName());
        };
    }

    @Override
    public <T extends Keyed> Registry<T> getRegistry(RegistryKey<T> registryKey) {
        throw NotImplementedException.createByLazy(RegistryAccess.class, "getRegistry", RegistryKey.class);
    }
}
