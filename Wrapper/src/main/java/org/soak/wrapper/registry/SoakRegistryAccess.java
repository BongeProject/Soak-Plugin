package org.soak.wrapper.registry;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.jspecify.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakBannerMap;
import org.soak.map.SoakBlockMap;
import org.soak.map.item.SoakEnchantmentTypeMap;
import org.soak.map.item.SoakPotionEffectMap;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.BannerPatternShapes;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.potion.PotionType;
import org.spongepowered.api.item.potion.PotionTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("NonExtendableApiUsage")
public class SoakRegistryAccess implements RegistryAccess {

    private final Map<RegistryKey<?>, SoakRegistry<?, ?>> registryMap = toMap(
            SoakRegistry.simple(RegistryKey.BANNER_PATTERN, BannerPatternShapes::registry, SoakBannerMap::toBukkit),
            SoakRegistry.simple(RegistryKey.POTION, PotionTypes::registry, SoakPotionEffectMap::toBukkit),
            SoakRegistry.simple(RegistryKey.ENCHANTMENT, EnchantmentTypes::registry, SoakEnchantmentTypeMap::toBukkit),
            SoakRegistry.simple(RegistryKey.STRUCTURE, Structure)
            SoakRegistry.simple(RegistryKey.BLOCK, BlockTypes::registry, SoakBlockMap::toBukkitType)
    );

    private Map<RegistryKey<?>, SoakRegistry<?, ?>> toMap(SoakRegistry<?, ?>... registries) {
        return Stream.of(registries).collect(Collectors.toMap(SoakRegistry::key, reg -> reg));
    }

    @Override
    public @Nullable <T extends Keyed> Registry<T> getRegistry(Class<T> aClass) {
        return switch (aClass.getSimpleName()) {
            case "Potion" -> (Registry<T>) getRegistry(RegistryKey.POTION);
            case "PatternType" -> (Registry<T>) getRegistry(RegistryKey.BANNER_PATTERN);
            case "Enchantment" -> (Registry<T>) getRegistry(RegistryKey.ENCHANTMENT);
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
