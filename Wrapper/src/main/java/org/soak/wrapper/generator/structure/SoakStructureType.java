package org.soak.wrapper.generator.structure;

import org.bukkit.NamespacedKey;
import org.bukkit.generator.structure.StructureType;
import org.jetbrains.annotations.NotNull;
import org.soak.map.SoakResourceKeyMap;
import org.spongepowered.api.registry.RegistryTypes;

public class SoakStructureType extends StructureType {

    private final org.spongepowered.api.world.generation.structure.StructureType type;

    public SoakStructureType(org.spongepowered.api.world.generation.structure.StructureType type) {
        this.type = type;
    }

    public org.spongepowered.api.world.generation.structure.StructureType sponge() {
        return this.type;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return SoakResourceKeyMap.mapToBukkit(this.type.key(RegistryTypes.STRUCTURE_TYPE));
    }
}
