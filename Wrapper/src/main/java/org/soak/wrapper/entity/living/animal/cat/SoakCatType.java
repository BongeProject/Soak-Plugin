package org.soak.wrapper.entity.living.animal.cat;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Cat;
import org.jetbrains.annotations.NotNull;
import org.soak.map.SoakResourceKeyMap;
import org.spongepowered.api.data.type.CatType;
import org.spongepowered.api.data.type.CatTypes;
import org.spongepowered.api.registry.RegistryTypes;

public class SoakCatType implements Cat.Type {

    private final CatType type;

    public SoakCatType(CatType type) {
        this.type = type;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return SoakResourceKeyMap.mapToBukkit(this.type.key(RegistryTypes.CAT_TYPE));
    }

    @Override
    public int compareTo(@NotNull Cat.Type type) {
        return this.getKey().asString().compareTo(type.getKey().asString());
    }

    @Override
    public @NotNull String name() {
        return getKey().value().toUpperCase();
    }

    @Override
    public int ordinal() {
        return CatTypes.registry().stream().toList().indexOf(this.type);
    }
}
