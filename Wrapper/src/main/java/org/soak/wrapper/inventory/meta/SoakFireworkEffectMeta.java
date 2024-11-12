package org.soak.wrapper.inventory.meta;

import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.map.SoakFireworkEffectMap;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackLike;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Collections;

public class SoakFireworkEffectMeta extends AbstractItemMeta implements FireworkEffectMeta {

    public SoakFireworkEffectMeta(ItemStackLike container) {
        super(container);
    }

    @Override
    public boolean hasEffect() {
        return !this.container.get(Keys.FIREWORK_EFFECTS).orElse(Collections.emptyList()).isEmpty();
    }

    //bukkit only supports a single firework effect -> get the first
    @Override
    public @Nullable FireworkEffect getEffect() {
        return this
                .container
                .get(Keys.FIREWORK_EFFECTS)
                .filter(effects -> !effects.isEmpty())
                .map(effects -> effects.get(0))
                .map(SoakFireworkEffectMap::toBukkit)
                .orElse(null);
    }

    //bukkit only supports a single firework effect -> override all to the provided new one
    @Override
    public void setEffect(@Nullable FireworkEffect effect) {
        if (effect == null) {
            this.setList(Keys.FIREWORK_EFFECTS, Collections.emptyList());
            return;
        }
        this.setList(Keys.FIREWORK_EFFECTS, Collections.singletonList(SoakFireworkEffectMap.buildSponge(effect)));
    }

    @Override
    public @NotNull SoakFireworkEffectMeta clone() {
        return new SoakFireworkEffectMeta(this.container.asImmutable());
    }
}
