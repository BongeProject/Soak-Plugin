package org.soak.data.type;

import org.bukkit.persistence.PersistentDataType;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;

import java.util.Optional;

public class BukkitBooleanDataType implements BukkitDataType<Boolean> {
    @Override
    public Optional<Boolean> get(DataView view, DataQuery from) {
        return view.getBoolean(from);
    }

    @Override
    public String typeName() {
        return "boolean";
    }

    @Override
    public Class<?> typeClass() {
        return Boolean.class;
    }

    @Override
    public DataContainer set(DataContainer container, DataQuery from, Boolean value) {
        return container.set(from, value);
    }

    @Override
    public PersistentDataType<?, Boolean> toBukkit() {
        return PersistentDataType.BOOLEAN;
    }
}
