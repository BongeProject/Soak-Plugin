package org.soak.map.event.inventory.action.craft;

import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.soak.WrapperManager;
import org.soak.map.event.EventSingleListenerWrapper;
import org.soak.map.item.SoakRecipeMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.inventory.view.AbstractInventoryView;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.CraftItemEvent;

public class SoakCraftItemEvent {

    private final EventSingleListenerWrapper<org.bukkit.event.inventory.CraftItemEvent> singleEventListener;

    public SoakCraftItemEvent(EventSingleListenerWrapper<org.bukkit.event.inventory.CraftItemEvent> wrapper) {
        this.singleEventListener = wrapper;
    }

    @Listener(order = Order.FIRST)
    public void firstEvent(CraftItemEvent.Preview event, @First ServerPlayer player) {
        fireEvent(event, player, EventPriority.HIGHEST);
    }

    @Listener(order = Order.EARLY)
    public void earlyEvent(CraftItemEvent.Preview event, @First ServerPlayer player) {
        fireEvent(event, player, EventPriority.HIGH);
    }

    @Listener(order = Order.DEFAULT)
    public void normalEvent(CraftItemEvent.Preview event, @First ServerPlayer player) {
        fireEvent(event, player, EventPriority.NORMAL);
    }

    @Listener(order = Order.LATE)
    public void lateEvent(CraftItemEvent.Preview event, @First ServerPlayer player) {
        fireEvent(event, player, EventPriority.LOWEST);
    }

    @Listener(order = Order.LAST)
    public void lastEvent(CraftItemEvent.Preview event, @First ServerPlayer player) {
        fireEvent(event, player, EventPriority.LOWEST);
    }

    private void fireEvent(CraftItemEvent.Preview event, ServerPlayer player, EventPriority priority) {
        var cause = event.cause();
        var spongeRecipe = event.recipe().orElseThrow(() -> new IllegalStateException("CraftItemEvent fired but no recipe found"));
        var bukkitRecipe = SoakRecipeMap.toBukkit(spongeRecipe);
        var inventoryView = AbstractInventoryView.wrap(player.openInventory().orElseThrow(() -> new IllegalStateException("CraftItemEvent fired but player's inventory isnt open")));
        var slot = event.slot().orElseThrow(() -> new RuntimeException("Cannot get slot"));
        var slotIndex = slot.get(Keys.SLOT_INDEX).orElseThrow(() -> new RuntimeException("Cannot get slot index"));
        var slotType = InventoryType.SlotType.CRAFTING;
        var clickType = ClickType.DROP; //TODO
        var action = InventoryAction.DROP_ONE_SLOT; //TODO

        var bEvent = new org.bukkit.event.inventory.CraftItemEvent(bukkitRecipe, inventoryView, slotType, slotIndex, clickType, action);
        SoakManager.<WrapperManager>getManager().getServer().getSoakPluginManager().callEvent(this.singleEventListener, bEvent, priority);
    }

}
