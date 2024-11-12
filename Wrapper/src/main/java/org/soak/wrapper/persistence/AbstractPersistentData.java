package org.soak.wrapper.persistence;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.soak.data.BukkitPersistentData;
import org.soak.data.sponge.SoakKeys;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakPersistentDataMap;
import org.soak.map.SoakResourceKeyMap;
import org.spongepowered.api.data.DataHolder;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractPersistentData<DH extends DataHolder> implements PersistentDataContainer {

    protected DH holder;

    protected AbstractPersistentData(DH holder) {
        this.holder = holder;
    }

    public DH getHolder() {
        return this.holder;
    }

    @Override
    public void copyTo(PersistentDataContainer persistentDataContainer, boolean replace) {
        throw NotImplementedException.createByLazy(PersistentDataContainer.class, "copyTo", PersistentDataContainer.class, boolean.class);
    }

    @Override
    public @NotNull Set<NamespacedKey> getKeys() {
        throw NotImplementedException.createByLazy(PersistentDataContainer.class, "getKeys");
    }

    @Override
    public <T, Z> boolean has(@NotNull NamespacedKey arg0, @NotNull PersistentDataType<T, Z> arg1) {
        return this.getOptional(arg0, arg1).isPresent();
    }

    @Override
    public @NotNull PersistentDataAdapterContext getAdapterContext() {
        throw NotImplementedException.createByLazy(PersistentDataContainer.class, "getAdapterContext");
    }

    @Override
    public <T, Z> Z get(@NotNull NamespacedKey arg0, @NotNull PersistentDataType<T, Z> arg1) {
        return getOptional(arg0, arg1).orElse(null);
    }

    private <T, Z> Optional<Z> getOptional(NamespacedKey bukkitKey, PersistentDataType<T, Z> bukkitType) {
        var key = SoakResourceKeyMap.mapToSponge(bukkitKey);
        var type = SoakPersistentDataMap.toSoak(bukkitType);
        BukkitPersistentData data = this.holder.get(SoakKeys.BUKKIT_DATA).orElseGet(BukkitPersistentData::new);
        return data.getValue(key, type);
    }

    @Override
    public boolean isEmpty() {
        throw NotImplementedException.createByLazy(PersistentDataContainer.class, "isEmpty");
    }

    @Override
    public <T, Z> @NotNull Z getOrDefault(@NotNull NamespacedKey arg0, @NotNull PersistentDataType<T, Z> arg1, @NotNull Z arg2) {
        return getOptional(arg0, arg1).orElse(arg2);
    }
}
