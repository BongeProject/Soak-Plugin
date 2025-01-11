package org.soak.wrapper.potion;

import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.jetbrains.annotations.NotNull;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakResourceKeyMap;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.Map;

public class SoakPotionEffectType extends PotionEffectType {

    org.spongepowered.api.effect.potion.PotionEffectType type;

    public SoakPotionEffectType(org.spongepowered.api.effect.potion.PotionEffectType type) {
        this.type = type;
    }

    @Override
    public double getDurationModifier() {
        throw NotImplementedException.createByLazy(PotionEffectType.class, "getDurationModifier");
    }

    @Override
    @Deprecated
    public int getId() {
        //not implemented really
        var effects = PotionEffectTypes.registry().stream().toList();
        return effects.indexOf(this.type);
    }

    @Override
    public @NotNull String getName() {
        return PlainTextComponentSerializer.plainText().serialize(this.type.asComponent());
    }

    @Override
    public @NotNull PotionEffect createEffect(int i, int i1) {
        throw NotImplementedException.createByLazy(PotionEffectType.class, "createEffect", int.class, int.class);
    }

    @Override
    public boolean isInstant() {
        return this.type.isInstant();
    }

    @Override
    public @NotNull PotionEffectTypeCategory getCategory() {
        throw NotImplementedException.createByLazy(PotionEffectType.class, "getCategory");
    }

    @Override
    public @NotNull Color getColor() {
        throw NotImplementedException.createByLazy(PotionEffectType.class, "getColor");
    }

    @Override
    public @NotNull Map<Attribute, AttributeModifier> getEffectAttributes() {
        throw NotImplementedException.createByLazy(PotionEffectType.class, "getEffectAttributes");
    }

    @Override
    public double getAttributeModifierAmount(@NotNull Attribute attribute, int i) {
        throw NotImplementedException.createByLazy(PotionEffectType.class, "getAttributeModifierAmount", Attribute.class, int.class);
    }

    @NotNull
    @Override
    public Category getEffectCategory() {
        throw NotImplementedException.createByLazy(PotionEffectType.class, "getEffectCategory");
    }

    @Override
    public @NotNull String translationKey() {
        var component = this.type.asComponent();
        if (!(component instanceof TranslatableComponent translate)) {
            throw new IllegalStateException("No translation key found");
        }

        return translate.key();
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return SoakResourceKeyMap.mapToBukkit(this.type.key(RegistryTypes.POTION_EFFECT_TYPE));
    }

    @Override
    public @NotNull String getTranslationKey() {
        return translationKey();
    }
}
