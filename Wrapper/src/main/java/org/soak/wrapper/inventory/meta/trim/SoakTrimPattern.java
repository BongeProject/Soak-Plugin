package org.soak.wrapper.inventory.meta.trim;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakResourceKeyMap;
import org.spongepowered.api.registry.RegistryTypes;

public class SoakTrimPattern implements TrimPattern {

    private final org.spongepowered.api.item.recipe.smithing.TrimPattern pattern;

    public SoakTrimPattern(org.spongepowered.api.item.recipe.smithing.TrimPattern pattern) {
        this.pattern = pattern;
    }

    public org.spongepowered.api.item.recipe.smithing.TrimPattern sponge() {
        return this.pattern;
    }

    @Override
    public @NotNull Component description() {
        throw NotImplementedException.createByLazy(TrimPattern.class, "description");
    }

    @Override
    public @NotNull String getTranslationKey() {
        throw NotImplementedException.createByLazy(TrimPattern.class, "getTranslationKey");
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return SoakResourceKeyMap.mapToBukkit(this.pattern.key(RegistryTypes.TRIM_PATTERN));
    }
}
