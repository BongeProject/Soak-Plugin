package org.soak.wrapper.inventory.meta;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.jetbrains.annotations.NotNull;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakResourceKeyMap;
import org.spongepowered.api.data.type.ArmorMaterial;
import org.spongepowered.api.registry.RegistryTypes;

public class SoakTrimMaterial implements TrimMaterial {

    private final ArmorMaterial armorMaterial;

    public SoakTrimMaterial(ArmorMaterial armorMaterial) {
        this.armorMaterial = armorMaterial;
    }

    @Override
    public @NotNull Component description() {
        throw NotImplementedException.createByLazy(TrimMaterial.class, "description");
    }

    @Override
    public @NotNull String getTranslationKey() {
        throw NotImplementedException.createByLazy(TrimMaterial.class, "getTranslationKey");
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return SoakResourceKeyMap.mapToBukkit(this.armorMaterial.key(RegistryTypes.ARMOR_MATERIAL));
    }
}
