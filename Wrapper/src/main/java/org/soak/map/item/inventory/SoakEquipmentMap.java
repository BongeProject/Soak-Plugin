package org.soak.map.item.inventory;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.soak.map.SoakResourceKeyMap;
import org.soak.utils.FakeRegistryHelper;
import org.soak.wrapper.inventory.meta.SoakTrimMaterial;
import org.soak.wrapper.inventory.meta.trim.SoakTrimPattern;
import org.spongepowered.api.data.type.ArmorMaterial;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.registry.DefaultedRegistryReference;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public class SoakEquipmentMap {

    public static TrimMaterial toBukkit(ArmorMaterial material) {
        NamespacedKey key = SoakResourceKeyMap.mapToBukkit(material.key(RegistryTypes.ARMOR_MATERIAL));
        Collection<TrimMaterial> collection = FakeRegistryHelper.getFields(TrimMaterial.class, TrimMaterial.class);
        return collection
                .stream()
                .filter(Objects::nonNull)
                .filter(t -> t.getKey().equals(key))
                .findAny()
                .orElseGet(() -> new SoakTrimMaterial(material));
    }

    public static TrimPattern toBukkit(org.spongepowered.api.item.recipe.smithing.TrimPattern trim) {
        NamespacedKey key = SoakResourceKeyMap.mapToBukkit(trim.key(RegistryTypes.TRIM_PATTERN));
        Collection<TrimPattern> collection = FakeRegistryHelper.getFields(TrimPattern.class, TrimPattern.class);

        return collection.stream().filter(pattern -> pattern.getKey().equals(key)).findAny().orElseGet(() -> new SoakTrimPattern(trim));
    }

    public static org.spongepowered.api.item.recipe.smithing.TrimPattern toSponge(TrimPattern pattern) {
        if (!(pattern instanceof SoakTrimPattern soak)) {
            throw new IllegalArgumentException("Trim Pattern must be a SoakTrimPattern");
        }
        return soak.sponge();
    }

    public static DefaultedRegistryReference<EquipmentType> toSponge(EquipmentSlot slot) {
        return switch (slot) {
            case HAND -> EquipmentTypes.MAINHAND;
            case OFF_HAND -> EquipmentTypes.OFFHAND;
            case FEET -> EquipmentTypes.FEET;
            case LEGS -> EquipmentTypes.LEGS;
            case CHEST -> EquipmentTypes.CHEST;
            case HEAD -> EquipmentTypes.HEAD;
            default -> throw new RuntimeException("No mapping for " + slot.name());
        };
    }

    public static Stream<EquipmentType> toSponge(EquipmentSlotGroup group) {
        if (group == EquipmentSlotGroup.ANY) {
            return EquipmentTypes.registry().stream();
        }
        if (group == EquipmentSlotGroup.HAND) {
            return Stream.of(EquipmentTypes.MAINHAND, EquipmentTypes.OFFHAND).map(DefaultedRegistryReference::get);
        }
        if(group == EquipmentSlotGroup.OFFHAND){
            return Stream.of(EquipmentTypes.OFFHAND.get());
        }
        throw new IllegalArgumentException("Cannot convert EquipmentSlotGroup of " + group.toString());
    }
}
