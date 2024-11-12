package org.soak.wrapper.entity.living.human;

import net.kyori.adventure.audience.Audience;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.service.permission.Subject;

public class SoakHumanEntity extends AbstractHumanBase<Human> {

    public SoakHumanEntity(Subject subject, Audience audience, Human entity) {
        super(subject, audience, entity);
    }

    @Override
    public @NotNull PlayerInventory getInventory() {
        throw NotImplementedException.createByLazy(SoakHumanEntity.class, "getInventory");
    }

    @Override
    public @Nullable InventoryView openInventory(@NotNull Inventory inventory) {
        throw NotImplementedException.createByLazy(SoakHumanEntity.class, "openInventory", Inventory.class);
    }

    @Override
    public void openInventory(@NotNull InventoryView inventory) {
        throw NotImplementedException.createByLazy(SoakHumanEntity.class, "openInventory", InventoryView.class);
    }

    @Override
    public void closeInventory() {
        throw NotImplementedException.createByLazy(SoakHumanEntity.class, "closeInventory");
    }

    @Override
    public void closeInventory(InventoryCloseEvent.@NotNull Reason reason) {
        throw NotImplementedException.createByLazy(SoakHumanEntity.class,
                "closeInventory",
                InventoryCloseEvent.Reason.class);
    }
}
