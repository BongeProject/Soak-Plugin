package org.soak.wrapper;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.map.SoakResourceKeyMap;
import org.spongepowered.api.registry.DefaultedRegistryType;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

public class SoakRegistry<SR, BR extends Keyed> implements Registry<BR> {

    private final DefaultedRegistryType<SR> registryType;
    private final Function<SR, BR> map;
    private final Class<BR> bukkitType;

    public SoakRegistry(DefaultedRegistryType<SR> registryType, Class<BR> bukkitType, Function<SR, BR> function) {
        this.registryType = registryType;
        this.map = function;
        this.bukkitType = bukkitType;
    }

    public Class<BR> bukkitType() {
        return this.bukkitType;
    }

    @Override
    public @Nullable BR get(@NotNull NamespacedKey namespacedKey) {
        return registryType.get().findEntry(SoakResourceKeyMap.mapToSponge(namespacedKey)).map(entry -> entry.value()).map(map).orElse(null);
    }

    @Override
    public @NotNull BR getOrThrow(@NotNull NamespacedKey namespacedKey) {
        return stream().filter(br -> br.getKey().equals(namespacedKey)).findAny().orElseThrow();
    }

    @Override
    public @NotNull Stream<BR> stream() {
        return registryType.get().stream().map(map);
    }

    @Override
    public @NotNull Iterator<BR> iterator() {
        return stream().iterator();
    }
}
