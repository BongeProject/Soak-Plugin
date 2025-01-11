package org.soak.wrapper.inventory.meta.food;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.map.item.SoakItemStackMap;
import org.soak.wrapper.inventory.meta.AbstractItemMeta;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.util.Ticks;

import java.util.List;
import java.util.Map;

public class SoakFoodComponent implements FoodComponent {

    private final AbstractItemMeta meta;

    public SoakFoodComponent(AbstractItemMeta meta) {
        this.meta = meta;
    }

    @Override
    public int getNutrition() {
        return this.meta.sponge().get(Keys.REPLENISHED_FOOD).orElse(0);
    }

    @Override
    public void setNutrition(int i) {
        this.meta.set(Keys.REPLENISHED_FOOD, i);
    }

    @Override
    public float getSaturation() {
        return this.meta.sponge().get(Keys.SATURATION).orElse(0.0).floatValue();
    }

    @Override
    public void setSaturation(float v) {
        this.meta.set(Keys.SATURATION, (double) v);
    }

    @Override
    public boolean canAlwaysEat() {
        return this.meta.sponge().get(Keys.CAN_ALWAYS_EAT).orElse(false);
    }

    @Override
    public void setCanAlwaysEat(boolean b) {
        this.meta.set(Keys.CAN_ALWAYS_EAT, b);
    }

    @Override
    public float getEatSeconds() {
        return this.meta.sponge().get(Keys.EATING_TIME).map(ticks -> ticks.ticks() / 20.0).orElse(0.0).floatValue();
    }

    @Override
    public void setEatSeconds(float v) {
        this.meta.set(Keys.EATING_TIME, Ticks.of((long) v * 20));
    }

    @Override
    public @Nullable ItemStack getUsingConvertsTo() {
        return this.meta.sponge().get(Keys.FOOD_CONVERTS_TO).map(SoakItemStackMap::toBukkit).orElse(null);
    }

    @Override
    public void setUsingConvertsTo(@Nullable ItemStack itemStack) {
        this.meta.set(Keys.FOOD_CONVERTS_TO, itemStack == null ? null : SoakItemStackMap.toSponge(itemStack));
    }

    @Override
    public @NotNull List<FoodEffect> getEffects() {
        throw NotImplementedException.createByLazy(FoodComponent.class, "getEffects");
    }

    @Override
    public void setEffects(@NotNull List<FoodEffect> list) {
        throw NotImplementedException.createByLazy(FoodComponent.class, "setEffects", List.class);
    }

    @Override
    public @NotNull FoodEffect addEffect(@NotNull PotionEffect potionEffect, float v) {
        throw NotImplementedException.createByLazy(FoodComponent.class, "addEffect", PotionEffect.class, float.class);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        throw NotImplementedException.createByLazy(FoodComponent.class, "serialize");
    }
}
