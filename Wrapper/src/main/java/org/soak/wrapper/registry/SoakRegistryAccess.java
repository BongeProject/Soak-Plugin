package org.soak.wrapper.registry;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.soak.wrapper.entity.living.animal.cat.SoakCatType;
import org.soak.wrapper.entity.living.villager.SoakVillagerProfession;
import org.soak.wrapper.entity.living.villager.SoakVillagerType;
import org.soak.wrapper.potion.SoakPotionEffectType;
import org.jspecify.annotations.Nullable;
import org.soak.map.*;
import org.soak.map.item.SoakPotionEffectMap;
import org.soak.map.item.inventory.SoakEquipmentMap;
import org.soak.wrapper.inventory.SoakMenuType;
import org.soak.wrapper.enchantment.SoakEnchantment;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.*;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.effect.sound.music.MusicDiscs;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ContainerTypes;
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
            SoakRegistry.simple(RegistryKey.ENCHANTMENT, EnchantmentTypes::registry, SoakEnchantment::new),
            SoakRegistry.simple(RegistryKey.STRUCTURE, Structures::registry, SoakStructureMap::toBukkit),
            SoakRegistry.simple(RegistryKey.STRUCTURE_TYPE, StructureTypes::registry, SoakStructureMap::toBukkit),
            SoakRegistry.simple(RegistryKey.TRIM_MATERIAL, ArmorMaterials::registry, SoakEquipmentMap::toBukkit),
            SoakRegistry.simple(RegistryKey.TRIM_PATTERN, TrimPatterns::registry, SoakEquipmentMap::toBukkit),
            SoakRegistry.simple(RegistryKey.DAMAGE_TYPE, DamageTypes::registry, SoakDamageMap::toBukkit),
            SoakRegistry.simple(RegistryKey.JUKEBOX_SONG, MusicDiscs::registry, SoakSoundMap::toBukkit),
            SoakRegistry.simple(RegistryKey.BLOCK, BlockTypes::registry, SoakBlockMap::toBukkitType),
            SoakRegistry.simple(RegistryKey.MENU, ContainerTypes::registry, SoakMenuType::new),
            SoakRegistry.simple(RegistryKey.MOB_EFFECT, PotionEffectTypes::registry, SoakPotionEffectType::new),
            SoakRegistry.simple(RegistryKey.CAT_VARIANT, CatTypes::registry, SoakCatType::new),
            SoakRegistry.simple(RegistryKey.VILLAGER_TYPE, VillagerTypes::registry, SoakVillagerType::new),
            SoakRegistry.simple(RegistryKey.VILLAGER_PROFESSION, ProfessionTypes::registry, SoakVillagerProfession::new),
            new SoakInvalidRegistry<>(RegistryKey.WOLF_VARIANT)
    );

    private Map<RegistryKey<?>, ISoakRegistry<?>> toMap(ISoakRegistry<?>... registries) {
        return Stream.of(registries).collect(Collectors.toMap(ISoakRegistry::key, reg -> reg));
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T extends Keyed> Registry<T> getRegistry(Class<T> aClass) {
        return switch (aClass.getCanonicalName()) {
            case "org.bukkit.block.banner.PatternType" -> (Registry<T>) getRegistry(RegistryKey.BANNER_PATTERN);
            case "org.bukkit.enchantments.Enchantment" -> (Registry<T>) getRegistry(RegistryKey.ENCHANTMENT);
            case "org.bukkit.generator.structure.Structure" -> (Registry<T>) getRegistry(RegistryKey.STRUCTURE);
            case "org.bukkit.inventory.meta.trim.StructureType" ->
                    (Registry<T>) getRegistry(RegistryKey.STRUCTURE_TYPE);
            case "org.bukkit.inventory.meta.trim.TrimMaterial" -> (Registry<T>) getRegistry(RegistryKey.TRIM_MATERIAL);
            case "org.bukkit.inventory.meta.trim.TrimPattern" -> (Registry<T>) getRegistry(RegistryKey.TRIM_PATTERN);
            case "org.bukkit.damage.DamageType" -> (Registry<T>) getRegistry(RegistryKey.DAMAGE_TYPE);
            case "org.bukkit.JukeboxSong" -> (Registry<T>) getRegistry(RegistryKey.JUKEBOX_SONG);
            case "org.bukkit.entity.Wolf.Variant" -> (Registry<T>) getRegistry(RegistryKey.WOLF_VARIANT);
            case "org.bukkit.entity.Cat.Type" -> (Registry<T>) getRegistry(RegistryKey.CAT_VARIANT);
            case "org.bukkit.entity.Villager$Profession" -> (Registry<T>) getRegistry(RegistryKey.VILLAGER_PROFESSION);
            case "org.bukkit.entity.Villager$Type" -> (Registry<T>) getRegistry(RegistryKey.VILLAGER_TYPE);
            case "org.bukkit.entity.memory.MemoryKey" -> (Registry<T>) getRegistry(RegistryKey.MEMORY_MODULE_TYPE);
            case "org.bukkit.Fluid" -> (Registry<T>) getRegistry(RegistryKey.FLUID);
            case "org.bukkit.entity.Frog.Variant" -> (Registry<T>) getRegistry(RegistryKey.FROG_VARIANT);
            case "org.bukkit.map.MapCursor.Type" -> (Registry<T>) getRegistry(RegistryKey.MAP_DECORATION_TYPE);
            case "org.bukkit.GameEvent" -> (Registry<T>) getRegistry(RegistryKey.GAME_EVENT);
            case "org.bukkit.inventory.MenuType" -> (Registry<T>) getRegistry(RegistryKey.MENU);
            default -> {
                System.err.println("Unknown registry for class: " + aClass.getCanonicalName());
                yield null;
            }
        };
    }

    @Override
    public <T extends Keyed> Registry<T> getRegistry(RegistryKey<T> registryKey) {
        return (Registry<T>) registryMap.get(registryKey);
    }
}
