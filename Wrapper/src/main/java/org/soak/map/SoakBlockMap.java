package org.soak.map;

import org.bukkit.Material;
import org.soak.generate.bukkit.MaterialList;
import org.spongepowered.api.block.BlockType;

import java.util.Arrays;
import java.util.Optional;

public class SoakBlockMap {

    public static Material toBukkit(BlockType type) {
        return MaterialList.value(type);
    }

    //well done Paper, might just come back to paper because of that change
    public static org.bukkit.block.BlockType toBukkitType(BlockType type) {
        //doing this way for the time being
        return toBukkit(type).asBlockType();
    }

    public static Optional<BlockType> toSponge(Material material) {
        return MaterialList.getBlockType(material);
    }
}
