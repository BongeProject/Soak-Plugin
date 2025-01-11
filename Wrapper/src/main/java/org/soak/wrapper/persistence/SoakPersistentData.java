package org.soak.wrapper.persistence;

import org.bukkit.persistence.PersistentDataContainer;
import org.soak.data.BukkitPersistentData;

public interface SoakPersistentData extends PersistentDataContainer {

    BukkitPersistentData wrapper();
}
