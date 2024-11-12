package org.soak.wrapper.inventory.meta;

import org.bukkit.Color;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakColourMap;
import org.soak.map.item.SoakPotionEffectMap;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.inventory.ItemStackLike;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SoakPotionItemMeta extends AbstractItemMeta implements PotionMeta {

    public SoakPotionItemMeta(ItemStackLike container) {
        super(container);
    }

    @Override
    public PotionData getBasePotionData() {
        var opPotionType = this.container.get(Keys.POTION_TYPE);
        if (opPotionType.isEmpty()) {
            return null;
        }
        //TODO something better
        return new PotionData(SoakPotionEffectMap.toBukkit(opPotionType.get()));
    }

    @Override
    public void setBasePotionData(PotionData arg0) {
        //TODO - find a better way to do this
        this.set(Keys.POTION_TYPE, SoakPotionEffectMap.toSponge(arg0.getType()));
    }

    @Override
    public @Nullable PotionType getBasePotionType() {
        throw NotImplementedException.createByLazy(PotionMeta.class, "getBasePotionType");
    }

    @Override
    public void setBasePotionType(@Nullable PotionType potionType) {
        throw NotImplementedException.createByLazy(PotionMeta.class, "setBasePotionType", PotionType.class);
    }

    @Override
    public boolean hasBasePotionType() {
        throw NotImplementedException.createByLazy(PotionMeta.class, "hasBasePotionType");
    }

    @Override
    public boolean hasCustomEffects() {
        return this.container.get(Keys.POTION_EFFECTS).filter(list -> !list.isEmpty()).isPresent();
    }

    @Override
    public @NotNull List<PotionEffect> getCustomEffects() {
        return this
                .container
                .get(Keys.POTION_EFFECTS)
                .orElse(Collections.emptyList())
                .stream()
                .map(SoakPotionEffectMap::toBukkit)
                .collect(Collectors.toList());
    }

    private boolean modifyEffect(Function<List<org.spongepowered.api.effect.potion.PotionEffect>, List<org.spongepowered.api.effect.potion.PotionEffect>> function) {
        var list = this.container.get(Keys.POTION_EFFECTS).orElse(new LinkedList<>());
        list = function.apply(list);
        this.setList(Keys.POTION_EFFECTS, list);
        return true;
    }

    @Override
    public boolean addCustomEffect(@NotNull PotionEffect effect, boolean overwrite) {
        if (overwrite) {
            removeCustomEffect(effect.getType());
        }
        return modifyEffect(spongeEffects -> {
            spongeEffects.add(SoakPotionEffectMap.buildSponge(effect));
            return spongeEffects;
        });
    }

    @Override
    public boolean removeCustomEffect(@NotNull PotionEffectType type) {
        var spongeEffect = SoakPotionEffectMap.toSponge(type);
        return this.modifyEffect(list -> list.stream().filter(effect -> !effect.type().equals(spongeEffect)).collect(Collectors.toList()));
    }

    @Override
    public boolean hasCustomEffect(@NotNull PotionEffectType type) {
        var spongeEffectType = SoakPotionEffectMap.toSponge(type);
        return this.container.get(Keys.POTION_EFFECTS).orElse(Collections.emptyList()).stream().anyMatch(effect -> effect.type().equals(spongeEffectType));
    }

    @Override
    public boolean setMainEffect(@NotNull PotionEffectType type) {
        throw NotImplementedException.createByLazy(PotionMeta.class, "setMainEffect", PotionEffectType.class);
    }

    @Override
    public boolean clearCustomEffects() {
        this.remove(Keys.POTION_EFFECTS);
        return true;
    }

    @Override
    public boolean hasColor() {
        return getColor() != null;
    }

    @Override
    public @Nullable Color getColor() {
        return this.container.get(Keys.COLOR).map(SoakColourMap::toBukkit).orElse(null);
    }

    @Override
    public void setColor(@Nullable Color color) {
        if (color == null) {
            this.remove(Keys.COLOR);
            return;
        }
        set(Keys.COLOR, SoakColourMap.toSponge(color));
    }

    @Override
    public @NotNull SoakPotionItemMeta clone() {
        return new SoakPotionItemMeta(this.container);
    }
}
