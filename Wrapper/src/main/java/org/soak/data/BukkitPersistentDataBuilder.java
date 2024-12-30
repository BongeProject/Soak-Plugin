package org.soak.data;

import org.soak.data.type.BukkitDataType;
import org.soak.data.type.BukkitDataTypes;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class BukkitPersistentDataBuilder implements DataBuilder<BukkitPersistentData> {
    @Override
    public Optional<BukkitPersistentData> build(DataView container) throws InvalidDataException {
        BukkitPersistentData data = new BukkitPersistentData();

        for (DataQuery pluginQuery : container.keys(false)) {
            Optional<DataView> opPluginView = container.getView(pluginQuery);
            if (opPluginView.isEmpty()) {
                continue;
            }
            for (DataQuery typeQuery : opPluginView.get().keys(false)) {
                Optional<DataView> opType = container.getView(typeQuery);
                if (opType.isEmpty()) {
                    continue;
                }
                Optional<BukkitData<Object>> opFoundData = getData(opType.get());
                if (opFoundData.isEmpty()) {
                    continue;
                }
                data.addValue(opFoundData.get());
            }
        }
        return Optional.of(data);
    }

    private <T> Optional<BukkitData<T>> getData(DataView view) {
        Optional<String> opPath = view.getString(BukkitData.PATH);
        Optional<String> opPlugin = view.getString(BukkitData.PLUGIN);
        Optional<BukkitDataType<T>> opType = view.getString(BukkitData.TYPE).flatMap(typeName -> BukkitDataTypes.TYPES.values().parallelStream().filter(type -> type.typeName().equals(typeName)).findAny().map(t -> (BukkitDataType<T>) t));

        if (opType.isEmpty() || opPath.isEmpty() || opPlugin.isEmpty()) {
            return Optional.empty();
        }

        Optional<T> opValue = opType.get().get(view, BukkitData.VALUE);
        return opValue.map(t -> new BukkitData<>(ResourceKey.of(opPlugin.get(), opPath.get()), t, opType.get()));
    }
}
