package org.soak.wrapper.inventory.view;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.WrapperManager;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakMessageMap;
import org.soak.map.item.SoakItemStackMap;
import org.soak.map.item.inventory.SoakInventoryMap;
import org.soak.plugin.SoakManager;
import org.soak.utils.ReflectionHelper;
import org.soak.wrapper.inventory.SoakInventory;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public class AbstractInventoryView implements InventoryView {

    private final Container spongeContainer;

    private AbstractInventoryView(Container container) {
        this.spongeContainer = container;
    }

    public static AbstractInventoryView wrap(Container container) {
        //TODO find out type
        return wrap(container, InventoryView.class);
    }

    public static AbstractInventoryView wrap(Container container, Class<? extends InventoryView> view) {
        //TODO other container views
        return new AbstractInventoryView(container);
    }

    public @NotNull Container sponge() {
        return this.spongeContainer;
    }

    @Override
    public @NotNull Inventory getTopInventory() {
        var inventory = SoakInventory.wrap(this.spongeContainer);
        inventory.setRequestedTitle(this.title());
        return inventory;
    }

    @Override
    public @NotNull Inventory getBottomInventory() {
        return getPlayer().getInventory();
    }

    @Override
    public @NotNull HumanEntity getPlayer() {
        return SoakManager.<WrapperManager>getManager().getMemoryStore().get(this.spongeContainer.viewer());
    }

    @Override
    public @NotNull InventoryType getType() {
        return SoakInventoryMap.toBukkit(this.spongeContainer);
    }

    @Override
    public void setItem(int i, @Nullable ItemStack itemStack) {
        //TODO check this
        var slot = this.spongeContainer.slot(i).orElseThrow(() -> new IndexOutOfBoundsException("Slot index of '" + i + "' is out of range"));
        if (itemStack == null) {
            slot.set(ItemStackSnapshot.empty());
            return;
        }
        var spongeStack = SoakItemStackMap.toSponge(itemStack);
        slot.set(spongeStack);
    }

    @Override
    public @Nullable ItemStack getItem(int i) {
        //TODO check this
        var spongeItem = this.spongeContainer.slot(i).orElseThrow(() -> new IndexOutOfBoundsException("Slot index of '" + i + "' is out of range")).peek();
        return SoakItemStackMap.toBukkit(spongeItem);
    }

    @Override
    public void setCursor(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            this.spongeContainer.setCursor(ItemStackSnapshot.empty());
            return;
        }
        var spongeItem = SoakItemStackMap.toSponge(itemStack);
        this.spongeContainer.setCursor(spongeItem);
    }

    @Override
    public @NotNull ItemStack getCursor() {
        var opSpongeItem = this.spongeContainer.cursor();
        return opSpongeItem.map(SoakItemStackMap::toBukkit).orElseGet(() -> new ItemStack(Material.AIR));
    }

    @Override
    public @Nullable Inventory getInventory(int i) {
        //TODO check this
        var opSlot = this.spongeContainer.slot(i);
        if (opSlot.isEmpty()) {
            return null;
        }
        var slot = opSlot.get();
        var isTopInventory = this.spongeContainer.viewed().stream().anyMatch(inv -> inv.containsChild(slot));
        if (isTopInventory) {
            return getTopInventory();
        }
        return getBottomInventory();


    }

    @Override
    public int convertSlot(int i) {
        //TODO check this
        return i;
    }

    @NotNull
    @Override
    public InventoryType.SlotType getSlotType(int i) {
        //TODO check this
        return SoakInventoryMap.toBukkit(spongeContainer.slot(i).orElseThrow(() -> new IndexOutOfBoundsException("'" + i + "' is out of bounds")));
    }

    @Override
    public void close() {
        var spongePlayer = this.spongeContainer.viewer();
        if (spongePlayer.openInventory().map(container -> container.equals(this.spongeContainer)).orElse(false)) {
            spongePlayer.closeInventory();
        }
    }

    @Override
    public int countSlots() {
        //TODO check this
        return this.spongeContainer.slots().size();
    }

    @Override
    public boolean setProperty(@NotNull InventoryView.Property property, int i) {
        throw NotImplementedException.createByLazy(InventoryView.class, "setProperty", InventoryView.Property.class, int.class);
    }

    @Override
    public @NotNull Component title() {
        var currentMenu = this.spongeContainer.currentMenu();
        if (currentMenu.isEmpty()) {
            //if remapped inventory -> it may return null here -> this forces it out
            var player = this.spongeContainer.viewer();
            try {
                var menuProvider = ReflectionHelper.getField(player, "inventory$menuProvider");
                var spongeWrappedComponent = ReflectionHelper.getField(menuProvider, "title");
                return ReflectionHelper.getField(spongeWrappedComponent, "wrapped");
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        var opTitle = currentMenu.get().title();
        return opTitle
                .orElseGet(() -> this.getType().defaultTitle());

    }

    @Override
    @Deprecated
    public @NotNull String getTitle() {
        return SoakMessageMap.mapToBukkit(title());
    }

    @Override
    public void setTitle(@NotNull String s) {
        this.spongeContainer.currentMenu().ifPresent(menu -> menu.setTitle(LegacyComponentSerializer.legacySection().deserialize(s)));
    }

    @Override
    public @NotNull String getOriginalTitle() {
        throw NotImplementedException.createByLazy(InventoryView.class, "getOriginalTitle");
    }
}
