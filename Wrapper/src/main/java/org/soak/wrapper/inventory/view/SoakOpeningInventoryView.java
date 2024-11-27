package org.soak.wrapper.inventory.view;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.wrapper.entity.living.human.SoakPlayer;

//used if a inventory has been requested to open, but another action is being done
public class SoakOpeningInventoryView implements InventoryView {

    private Inventory top;
    private SoakPlayer player;
    private String title;

    public SoakOpeningInventoryView(Inventory top, SoakPlayer player, String title) {
        this.top = top;
        this.player = player;
        this.title = title;
    }

    @Override
    public @NotNull Inventory getTopInventory() {
        return top;
    }

    @Override
    public @NotNull Inventory getBottomInventory() {
        return getPlayer().getInventory();
    }

    @Override
    public @NotNull SoakPlayer getPlayer() {
        return this.player;
    }

    @Override
    public @NotNull InventoryType getType() {
        return this.top.getType();
    }

    @Override
    public void setItem(int i, @Nullable ItemStack itemStack) {
        throw NotImplementedException.createByLazy(InventoryView.class, "setItem", int.class, ItemStack.class);
    }

    @Override
    public @Nullable ItemStack getItem(int i) {
        throw NotImplementedException.createByLazy(InventoryView.class, "getItem", int.class);

    }

    @Override
    public void setCursor(@Nullable ItemStack itemStack) {
        throw NotImplementedException.createByLazy(InventoryView.class, "setCursor", ItemStack.class);

    }

    @Override
    public @NotNull ItemStack getCursor() {
        throw NotImplementedException.createByLazy(InventoryView.class, "getCursor");

    }

    @Override
    public @Nullable Inventory getInventory(int i) {
        throw NotImplementedException.createByLazy(InventoryView.class, "setInventory", int.class);
    }

    @Override
    public int convertSlot(int i) {
        throw NotImplementedException.createByLazy(InventoryView.class, "convertSlot", int.class);
    }

    @NotNull
    @Override
    public InventoryType.SlotType getSlotType(int i) {
        throw NotImplementedException.createByLazy(InventoryView.class, "getSlotType", int.class);
    }

    @Override
    public void close() {
        if (this.player.getOpenInventory().getTopInventory().equals(this.top)) {
            this.player.closeInventory();
        }
    }

    @Override
    public int countSlots() {
        throw NotImplementedException.createByLazy(InventoryView.class, "countSlots");
    }

    @Override
    public boolean setProperty(@NotNull InventoryView.Property property, int i) {
        throw NotImplementedException.createByLazy(InventoryView.class, "setProperty", InventoryView.Property.class, int.class);

    }

    @Override
    public @NotNull String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(@NotNull String s) {
        this.title = s;
    }

    @Override
    public @NotNull String getOriginalTitle() {
        throw NotImplementedException.createByLazy(InventoryView.class, "getOriginalTitle");
    }
}
