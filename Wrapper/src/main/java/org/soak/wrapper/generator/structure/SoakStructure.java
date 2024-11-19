package org.soak.wrapper.generator.structure;

import org.bukkit.NamespacedKey;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.jetbrains.annotations.NotNull;
import org.soak.map.SoakResourceKeyMap;
import org.soak.map.SoakStructureMap;
import org.spongepowered.api.registry.RegistryTypes;

public class SoakStructure extends Structure {

    private final org.spongepowered.api.world.generation.structure.Structure structure;

    public SoakStructure(org.spongepowered.api.world.generation.structure.Structure structure) {
        this.structure = structure;
    }

    public org.spongepowered.api.world.generation.structure.Structure sponge(){
        return this.structure;
    }

    @Override
    public @NotNull StructureType getStructureType() {
        return SoakStructureMap.toBukkit(this.structure.type());
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        var key = structure.key(RegistryTypes.STRUCTURE);
        return SoakResourceKeyMap.mapToBukkit(key);
    }
}
