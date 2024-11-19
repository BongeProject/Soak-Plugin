package org.soak.wrapper.registry;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.jspecify.annotations.Nullable;
import org.soak.map.*;
import org.soak.map.item.SoakEnchantmentTypeMap;
import org.soak.map.item.SoakPotionEffectMap;
import org.soak.map.item.inventory.SoakEquipmentMap;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.ArmorMaterials;
import org.spongepowered.api.data.type.BannerPatternShapes;
import org.spongepowered.api.effect.sound.music.MusicDiscs;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.potion.PotionTypes;
import org.spongepowered.api.item.recipe.smithing.TrimPatterns;
import org.spongepowered.api.world.generation.structure.StructureTypes;
import org.spongepowered.api.world.generation.structure.Structures;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("NonExtendableApiUsage")
public class SoakRegistryAccess implements RegistryAccess {

    private final Map<RegistryKey<?>, ISoakRegistry<?>> registryMap = toMap(
            SoakRegistry.simple(RegistryKey.BANNER_PATTERN, BannerPatternShapes::registry, SoakBannerMap::toBukkit),
            SoakRegistry.simple(RegistryKey.POTION, PotionTypes::registry, SoakPotionEffectMap::toBukkit),
            SoakRegistry.simple(RegistryKey.ENCHANTMENT, EnchantmentTypes::registry, SoakEnchantmentTypeMap::toBukkit),
            SoakRegistry.simple(RegistryKey.STRUCTURE, Structures::registry, SoakStructureMap::toBukkit),
            SoakRegistry.simple(RegistryKey.STRUCTURE_TYPE, StructureTypes::registry, SoakStructureMap::toBukkit),
            SoakRegistry.simple(RegistryKey.TRIM_MATERIAL, ArmorMaterials::registry, SoakEquipmentMap::toBukkit),
            SoakRegistry.simple(RegistryKey.TRIM_PATTERN, TrimPatterns::registry, SoakEquipmentMap::toBukkit),
            SoakRegistry.simple(RegistryKey.DAMAGE_TYPE, DamageTypes::registry, SoakDamageMap::toBukkit),
            SoakRegistry.simple(RegistryKey.JUKEBOX_SONG, MusicDiscs::registry, SoakSoundMap::toBukkit),
            SoakRegistry.simple(RegistryKey.BLOCK, BlockTypes::registry, SoakBlockMap::toBukkitType),
            new SoakInvalidRegistry<>(RegistryKey.WOLF_VARIANT)
    );

    private Map<RegistryKey<?>, ISoakRegistry<?>> toMap(ISoakRegistry<?>... registries) {
        return Stream.of(registries).collect(Collectors.toMap(ISoakRegistry::key, reg -> reg));
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T extends Keyed> Registry<T> getRegistry(Class<T> aClass) {
        return switch (aClass.getSimpleName()) {
            case "Potion" -> (Registry<T>) getRegistry(RegistryKey.POTION);
            case "PatternType" -> (Registry<T>) getRegistry(RegistryKey.BANNER_PATTERN);
            case "Enchantment" -> (Registry<T>) getRegistry(RegistryKey.ENCHANTMENT);
            case "Structure" -> (Registry<T>) getRegistry(RegistryKey.STRUCTURE);
            case "StructureType" -> (Registry<T>) getRegistry(RegistryKey.STRUCTURE_TYPE);
            case "TrimMaterial" -> (Registry<T>) getRegistry(RegistryKey.TRIM_MATERIAL);
            case "TrimPattern" -> (Registry<T>) getRegistry(RegistryKey.TRIM_PATTERN);
            case "DamageType" -> (Registry<T>) getRegistry(RegistryKey.DAMAGE_TYPE);
            case "JukeboxSong" -> (Registry<T>) getRegistry(RegistryKey.JUKEBOX_SONG);
            case "Wolf$Variant" -> (Registry<T>) getRegistry(RegistryKey.WOLF_VARIANT);
            default -> {
                System.err.println("Unknown registry for class: " + aClass.getSimpleName());
                yield null;
            }
        };
    }

    @Override
    public <T extends Keyed> Registry<T> getRegistry(RegistryKey<T> registryKey) {
        return (Registry<T>) registryMap.get(registryKey);
    }
}
