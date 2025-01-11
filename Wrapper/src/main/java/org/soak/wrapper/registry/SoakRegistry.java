package org.soak.wrapper.registry;

import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.registry.Registry;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SoakRegistry<SpongeType, R extends Keyed> implements ISoakRegistry<R> {

    private final Function<Registry<SpongeType>, Stream<R>> to;
    private final Supplier<Registry<SpongeType>> reg;
    private final RegistryKey<R> bukkitKey;

    SoakRegistry(RegistryKey<R> key, Supplier<Registry<SpongeType>> supplier, Function<Registry<SpongeType>, Stream<R>> to) {
        this.to = to;
        this.reg = supplier;
        this.bukkitKey = key;
    }

    static <ST, BR extends Keyed> SoakRegistry<ST, BR> simple(RegistryKey<BR> key, Supplier<Registry<ST>> supplier, Function<ST, BR> map) {
        return new SoakRegistry<>(key, supplier, reg -> reg.stream().map(map));
    }

    public RegistryKey<R> key() {
        return this.bukkitKey;
    }

    @Override
    public @Nullable R get(@NotNull NamespacedKey namespacedKey) {
        return stream().filter(key -> key.getKey().equals(namespacedKey)).findAny().orElse(null);
    }

    @Override
    public @NotNull R getOrThrow(@NotNull NamespacedKey namespacedKey) {
        return stream().filter(key -> key.getKey().equals(namespacedKey)).findAny().orElseThrow(() -> new IllegalArgumentException("Could not find the value with the key: " + namespacedKey.asString()));
    }

    @Override
    public @NotNull Stream<R> stream() {
        return this.to.apply(this.reg.get());
    }

    @NotNull
    @Override
    public Iterator<R> iterator() {
        return stream().iterator();
    }
}
