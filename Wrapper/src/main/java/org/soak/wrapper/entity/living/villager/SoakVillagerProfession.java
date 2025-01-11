package org.soak.wrapper.entity.living.villager;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;
import org.soak.map.SoakResourceKeyMap;
import org.spongepowered.api.data.type.ProfessionType;
import org.spongepowered.api.data.type.ProfessionTypes;
import org.spongepowered.api.data.type.VillagerType;
import org.spongepowered.api.data.type.VillagerTypes;
import org.spongepowered.api.registry.RegistryTypes;

public class SoakVillagerProfession implements Villager.Profession {

    private final ProfessionType type;

    public SoakVillagerProfession(ProfessionType type) {
        this.type = type;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return SoakResourceKeyMap.mapToBukkit(this.type.key(RegistryTypes.PROFESSION_TYPE));
    }

    @Override
    public int compareTo(@NotNull Villager.Profession type) {
        return this.getKey().asString().compareTo(type.getKey().asString());
    }

    @Override
    public @NotNull String name() {
        return getKey().value().toUpperCase();
    }

    @Override
    public int ordinal() {
        return ProfessionTypes.registry().stream().toList().indexOf(this.type);
    }
}
