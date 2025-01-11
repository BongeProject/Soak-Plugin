package org.soak.wrapper.persistence;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class SoakPersistentDataContext implements PersistentDataAdapterContext {
    @Override
    public @NotNull PersistentDataContainer newPersistentDataContainer() {
        return new FakePersistentDataContainer();
    }
}
