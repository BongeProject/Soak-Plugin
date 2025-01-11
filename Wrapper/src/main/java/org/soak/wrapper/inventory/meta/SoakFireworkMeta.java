package org.soak.wrapper.inventory.meta;

import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakFireworkEffectMap;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.entity.projectile.explosive.FireworkRocket;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackLike;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.Ticks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SoakFireworkMeta extends AbstractItemMeta implements FireworkMeta {

    public SoakFireworkMeta(ItemStack stack) {
        super(stack);
    }

    public SoakFireworkMeta(ItemStackSnapshot stack) {
        super(stack);
    }

    //move to own class
    /*public SoakFireworkMeta(FireworkRocket entity) {
        super(entity);
    }*/

    public SoakFireworkMeta(ItemStackLike container) {
        super(container);
    }

    @Override
    public boolean hasEffects() {
        return !this.container.get(Keys.FIREWORK_EFFECTS).orElse(Collections.emptyList()).isEmpty();
    }

    @Override
    public boolean hasPower() {
        throw NotImplementedException.createByLazy(FireworkMeta.class, "hasPower");
    }

    @Override
    public int getPower() {
        return this.container.get(Keys.FIREWORK_FLIGHT_MODIFIER).map(tick -> (int) tick.ticks()).orElse(-1);
    }

    @Override
    public void setPower(int power) throws IllegalArgumentException {
        this.set(Keys.FIREWORK_FLIGHT_MODIFIER, Ticks.of(power));
    }

    @Override
    public @NotNull List<FireworkEffect> getEffects() {
        return this
                .container
                .get(Keys.FIREWORK_EFFECTS)
                .map(effects -> effects.stream()
                        .map(SoakFireworkEffectMap::toBukkit)
                        .collect(Collectors.toList())
                )
                .orElse(Collections.emptyList());
    }

    @Override
    public void clearEffects() {
        this.setList(Keys.FIREWORK_EFFECTS, Collections.emptyList());
    }

    @Override
    public void removeEffect(int index) throws IndexOutOfBoundsException {
        var currentEffects = new ArrayList<>(this.container.get(Keys.FIREWORK_EFFECTS).orElse(Collections.emptyList()));
        currentEffects.remove(index);
        this.setList(Keys.FIREWORK_EFFECTS, currentEffects);
    }

    @Override
    public void addEffects(@NotNull FireworkEffect... effects) throws IllegalArgumentException {
        addEffects(Arrays.asList(effects));
    }

    @Override
    public void addEffects(@NotNull Iterable<FireworkEffect> effects) throws IllegalArgumentException {
        var toAddSponge = StreamSupport.stream(effects.spliterator(), false)
                .map(SoakFireworkEffectMap::buildSponge)
                .collect(Collectors.toList());
        var currentSponge = new ArrayList<>(this.container.get(Keys.FIREWORK_EFFECTS).orElse(Collections.emptyList()));
        currentSponge.addAll(toAddSponge);
        this.setList(Keys.FIREWORK_EFFECTS, currentSponge);
    }

    @Override
    public int getEffectsSize() {
        return this.getEffects().size();
    }

    //bukkit only supports a single firework effect -> override all to the provided new one
    @Override
    public void addEffect(@Nullable FireworkEffect effect) {
        if (effect == null) {
            this.setList(Keys.FIREWORK_EFFECTS, Collections.emptyList());
            return;
        }
        this.setList(Keys.FIREWORK_EFFECTS, Collections.singletonList(SoakFireworkEffectMap.buildSponge(effect)));
    }

    @Override
    public @NotNull SoakFireworkMeta clone() {
        return new SoakFireworkMeta(this.container.asImmutable());
    }
}
