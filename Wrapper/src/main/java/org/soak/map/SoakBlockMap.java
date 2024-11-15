package org.soak.map;

import org.bukkit.Material;
import org.soak.generate.bukkit.MaterialList;
import org.spongepowered.api.block.BlockType;

import java.util.Optional;

public class SoakBlockMap {

    public static Material toBukkit(BlockType type) {
        return MaterialList.value(type);
    }

    public static Optional<BlockType> toSponge(Material material) {
        return MaterialList.getBlockType(material);
    }
}
