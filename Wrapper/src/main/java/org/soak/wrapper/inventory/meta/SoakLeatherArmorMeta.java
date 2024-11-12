package org.soak.wrapper.inventory.meta;

import org.bukkit.Color;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.map.SoakColourMap;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.item.inventory.ItemStackLike;

public class SoakLeatherArmorMeta extends AbstractItemMeta implements LeatherArmorMeta {
    public SoakLeatherArmorMeta(ItemStackLike container) {
        super(container);
    }

    @Override
    public @NotNull Color getColor() {
        return this
                .container
                .get(Keys.COLOR)
                .map(SoakColourMap::toBukkit)
                .orElseThrow(() -> new RuntimeException("Cannot read leather colour of " + this.container.toString()));
    }

    @Override
    public void setColor(@Nullable Color color) {
        if (color == null) {
            this.remove(Keys.COLOR);
            return;
        }
        var spongeColour = SoakColourMap.toSponge(color);
        this.set(Keys.COLOR, spongeColour);
    }

    @Override
    public @NotNull SoakLeatherArmorMeta clone() {
        return new SoakLeatherArmorMeta(this.container);
    }

    @Override
    public boolean isDyed() {
        return this.container.get(Keys.COLOR).isPresent();
    }
}
