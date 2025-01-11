package org.soak.data;

import org.soak.data.type.BukkitDataType;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataSerializable;
import org.spongepowered.api.data.persistence.DataView;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.LinkedTransferQueue;
import java.util.stream.Collectors;

public class BukkitPersistentData implements DataSerializable {

    public static int CONTENT_VERSION = 1;
    Collection<BukkitData<?>> data = new LinkedTransferQueue<>();

    public Map<String, DataView> toMap() {
        return data.stream().collect(Collectors.toMap(data -> data.getKey().formatted(), data -> {
            var dataContainer = DataContainer.createNew();
            dataContainer.set(DataQuery.of("type"), data.getType().typeName());
            dataContainer.set(DataQuery.of("value"), data.getValue());
            return dataContainer;
        }));
    }

    public <T> Optional<T> getValue(ResourceKey key) {
        return getData(key).map(data -> (T) data.getValue());
    }

    private Optional<BukkitData<?>> getData(ResourceKey key) {
        return this.data.parallelStream().filter(data -> data.getKey().equals(key)).findAny();
    }

    public <T> Optional<T> getValue(ResourceKey key, BukkitDataType<T> dataType) {
        return getData(key).filter(data -> data.getType().equals(dataType)).map(data -> (T) data.getValue());
    }

    public void removeValue(ResourceKey key) {
        this.data.parallelStream().filter(data -> data.getKey().equals(key)).forEach(data -> this.data.remove(data));

    }


    public <T> void addValue(ResourceKey key, BukkitDataType<T> type, T value) {
        var data = new BukkitData<>(key, value, type);
        this.data.add(data);
    }

    public void addValue(BukkitData<?> data) {
        this.data.add(data);
    }

    @Override
    public int contentVersion() {
        return CONTENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        var container = DataContainer.createNew();
        for (BukkitData<?> data : this.data) {
            container = data.set(container, DataQuery.of(data.getKey().namespace(), data.getKey().value()));
        }
        return container;
    }
}
