package org.soak.data.type;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.soak.wrapper.persistence.FakePersistentDataContainer;
import org.soak.wrapper.persistence.SoakPersistentData;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;

import java.util.Optional;
import java.util.function.BiConsumer;

public class BukkitDataContainerDataType implements BukkitDataType<PersistentDataContainer> {
    @Override
    public Optional<PersistentDataContainer> get(DataView view, DataQuery from) {
        return view.getMap(from).map(map -> {
            var persistent = new FakePersistentDataContainer();
            map.forEach((key, value) -> convert(value, (dataType, v) -> persistent.set(NamespacedKey.fromString(key.toString()), dataType, v)));
            return persistent;
        });
    }

    private <P, C> void convert(Object obj, BiConsumer<PersistentDataType<P, C>, C> consumer) {
        var entry = (DataView) obj;
        var value = (C) entry.get(DataQuery.of("value")).orElseThrow(() -> new IllegalArgumentException("Cannot find value"));
        var type = entry.get(DataQuery.of("type")).orElseThrow(() -> new IllegalArgumentException("Cannot find type"));
        PersistentDataType<P, C> dataType = (PersistentDataType<P, C>) BukkitDataTypes.TYPES.values().stream().filter(bukkitDataType -> bukkitDataType.typeName().equals(type)).findAny().orElseThrow(() -> new IllegalArgumentException("Unable to get Bukkit data mapping for " + type)).toBukkit();
        consumer.accept(dataType, value);
    }

    @Override
    public String typeName() {
        return typeClass().getSimpleName();
    }

    @Override
    public Class<?> typeClass() {
        return PersistentDataContainer.class;
    }

    @Override
    public boolean isType(Class<?> classType) {
        return classType == PersistentDataContainer.class;
    }

    @Override
    public DataContainer set(DataContainer container, DataQuery from, PersistentDataContainer value) {
        if (!(value instanceof SoakPersistentData soak)) {
            throw new IllegalArgumentException("Unknown PersistentDataContainer implementation of " + value.getClass().getTypeName());
        }
        var map = soak.wrapper().toMap();
        return container.set(from, map);
    }

    @Override
    public PersistentDataType<?, PersistentDataContainer> toBukkit() {
        return PersistentDataType.TAG_CONTAINER;
    }
}
