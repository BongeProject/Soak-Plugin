package org.soak.map;

import org.bukkit.NamespacedKey;
import org.soak.utils.FakeRegistryHelper;
import org.soak.wrapper.generator.structure.SoakStructure;
import org.soak.wrapper.generator.structure.SoakStructureType;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.generation.structure.Structure;
import org.spongepowered.api.world.generation.structure.StructureType;

import java.util.Collection;
import java.util.Objects;

public class SoakStructureMap {

    public static Structure toSponge(org.bukkit.generator.structure.Structure structure) {
        if (!(structure instanceof SoakStructure soak)) {
            throw new IllegalArgumentException("Structure must be SoakStructure");
        }
        return soak.sponge();
    }

    public static StructureType toSponge(org.bukkit.generator.structure.StructureType structureType) {
        if (!(structureType instanceof SoakStructureType soak)) {
            throw new IllegalArgumentException("Structure Type must be SoakStructureType");
        }
        return soak.sponge();
    }

    public static org.bukkit.generator.structure.Structure toBukkit(Structure structure) {
        NamespacedKey namespace = SoakResourceKeyMap.mapToBukkit(structure.key(RegistryTypes.STRUCTURE));
        Collection<org.bukkit.generator.structure.Structure> fakeReg = FakeRegistryHelper.getFields(org.bukkit.generator.structure.Structure.class, org.bukkit.generator.structure.Structure.class);
        var opConstant = fakeReg.stream().filter(Objects::nonNull).filter(str -> str.getKey().equals(namespace)).findAny();
        //orElseGet should only be used on the register
        return opConstant.orElseGet(() -> new SoakStructure(structure));
    }


    public static org.bukkit.generator.structure.StructureType toBukkit(StructureType structure) {
        NamespacedKey namespace = SoakResourceKeyMap.mapToBukkit(structure.key(RegistryTypes.STRUCTURE));
        Collection<org.bukkit.generator.structure.StructureType> fakeReg = FakeRegistryHelper.getFields(org.bukkit.generator.structure.StructureType.class, org.bukkit.generator.structure.StructureType.class);
        var opConstant = fakeReg.stream().filter(Objects::nonNull).filter(str -> str.getKey().equals(namespace)).findAny();
        //orElseGet should only be used on the register
        return opConstant.orElseGet(() -> new SoakStructureType(structure));
    }
}
