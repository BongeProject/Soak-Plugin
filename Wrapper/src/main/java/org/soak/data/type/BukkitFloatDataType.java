package org.soak.data.type;

import org.bukkit.persistence.PersistentDataType;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;

import java.util.Optional;

public class BukkitFloatDataType implements BukkitDataType<Float> {
    @Override
    public Optional<Float> get(DataView view, DataQuery from) {
        return view.getFloat(from);
    }

    @Override
    public String typeName() {
        return "float";
    }

    @Override
    public Class<?> typeClass() {
        return float.class;
    }

    @Override
    public boolean isType(Class<?> classType) {
        return classType == float.class || classType == Float.class;
    }

    @Override
    public DataContainer set(DataContainer container, DataQuery from, Float value) {
        return container.set(from, value);
    }

    @Override
    public PersistentDataType<?, Float> toBukkit() {
        return PersistentDataType.FLOAT;
    }
}
