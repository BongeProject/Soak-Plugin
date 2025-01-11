package org.soak.map;

import org.bukkit.persistence.PersistentDataType;
import org.soak.data.type.BukkitDataType;
import org.soak.data.type.BukkitDataTypes;

public class SoakPersistentDataMap {

    public static <T> BukkitDataType<T> toSoak(PersistentDataType<?, T> dataType) {
        return BukkitDataTypes.TYPES
                .values()
                .parallelStream()
                .filter(type -> type.toBukkit().equals(dataType))
                .findAny()
                .map(type -> (BukkitDataType<T>) type)
                .orElseThrow(() -> new RuntimeException("No direct mapping for " + dataType.getPrimitiveType()
                        .getSimpleName()));
    }
}
