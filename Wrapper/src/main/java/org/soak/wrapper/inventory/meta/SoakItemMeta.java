package org.soak.wrapper.inventory.meta;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackLike;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public class SoakItemMeta extends AbstractItemMeta {

    public SoakItemMeta(ItemStackLike container) {
        super(container);
    }


    @Override
    public @NotNull SoakItemMeta clone() {
        return new SoakItemMeta(this.container.asImmutable());
    }
}
