package org.soak.map;

import org.bukkit.block.Biome;
import org.spongepowered.api.registry.RegistryReference;
import org.spongepowered.api.world.biome.Biomes;

public class SoakBiomeMap {

    public static Biome toBukkit(org.spongepowered.api.world.biome.Biome sponge) {
        System.err.println("Biome requires ByteBuddy attention");
        return Biome.PLAINS;
    }

    public static org.spongepowered.api.world.biome.Biome toSponge(Biome biome) {
        return toSpongeGetter(biome).get();
    }

    public static RegistryReference<org.spongepowered.api.world.biome.Biome> toSpongeGetter(Biome bukkit) {
        System.err.println("Biome requires ByteBuddy attention");
        return Biomes.PLAINS;
    }
}
