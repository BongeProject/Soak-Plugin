package org.soak.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.value.Value;

import java.util.Map;

public class KeyValuePair<Val> implements Map.Entry<Key<? extends Value<Val>>, Val>, Cloneable {
    private final @NotNull Key<? extends Value<Val>> key;
    private @Nullable Val value;

    public KeyValuePair(@NotNull Key<? extends Value<Val>> key, @Nullable Val value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Key<? extends Value<Val>> getKey() {
        return this.key;
    }

    @Override
    public Val getValue() {
        return this.value;
    }

    @Override
    public Val setValue(Val value) {
        var ret = this.value;
        this.value = value;
        return ret;
    }

    @Override
    public KeyValuePair<Val> clone() {
        return new KeyValuePair<>(this.key, this.value);
    }

    public DataTransactionResult apply(DataHolder.Mutable mutable) {
        return mutable.offer(this.key, this.value);
    }
}
