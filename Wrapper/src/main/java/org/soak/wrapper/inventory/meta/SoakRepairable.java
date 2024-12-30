package org.soak.wrapper.inventory.meta;

import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.inventory.ItemStackLike;

public class SoakRepairable extends AbstractItemMeta implements Repairable {

    public SoakRepairable(ItemStackLike container) {
        super(container);
    }

    @Override
    public boolean hasRepairCost() {
        return this.container.get(Keys.REPAIR_COST).isPresent();
    }

    @Override
    public int getRepairCost() {
        return this.container.get(Keys.REPAIR_COST).orElse(0);
    }

    @Override
    public void setRepairCost(int i) {
        set(Keys.REPAIR_COST, i);
    }

    @Override
    public @NotNull SoakRepairable clone() {
        return new SoakRepairable(this.container.asMutableCopy());
    }
}
