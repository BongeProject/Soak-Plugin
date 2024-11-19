package org.soak.wrapper.registry;

import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.stream.Stream;

public class SoakInvalidRegistry<B extends Keyed> implements ISoakRegistry<B> {

    private RegistryKey<B> key;

    public SoakInvalidRegistry(RegistryKey<B> key) {
        this.key = key;
    }

    @Override
    public @Nullable B get(@NotNull NamespacedKey namespacedKey) {
        return null;
    }

    @Override
    public @NotNull B getOrThrow(@NotNull NamespacedKey namespacedKey) {
        throw new IllegalArgumentException("Registry is not enabled");
    }

    @Override
    public @NotNull Stream<B> stream() {
        return Stream.empty();
    }

    @Override
    public @NotNull Iterator<B> iterator() {
        return stream().iterator();
    }

    @Override
    public RegistryKey<B> key() {
        return this.key;
    }
}
