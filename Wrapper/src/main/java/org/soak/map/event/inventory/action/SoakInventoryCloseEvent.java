package org.soak.map.event.inventory.action;

import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.soak.WrapperManager;
import org.soak.map.event.EventSingleListenerWrapper;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.inventory.view.AbstractInventoryView;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.item.inventory.container.InteractContainerEvent;

public class SoakInventoryCloseEvent {

    private final EventSingleListenerWrapper<InventoryCloseEvent> singleEventListener;

    public SoakInventoryCloseEvent(EventSingleListenerWrapper<InventoryCloseEvent> singleEventListener) {
        this.singleEventListener = singleEventListener;
    }

    @Listener(order = Order.FIRST)
    public void firstEvent(InteractContainerEvent.Close spongeEvent) {
        fireEvent(spongeEvent, EventPriority.HIGHEST);
    }

    @Listener(order = Order.EARLY)
    public void earlyEvent(InteractContainerEvent.Close spongeEvent) {
        fireEvent(spongeEvent, EventPriority.HIGH);
    }

    @Listener(order = Order.DEFAULT)
    public void normalEvent(InteractContainerEvent.Close spongeEvent) {
        fireEvent(spongeEvent, EventPriority.NORMAL);
    }

    @Listener(order = Order.LATE)
    public void lateEvent(InteractContainerEvent.Close spongeEvent) {
        fireEvent(spongeEvent, EventPriority.LOW);
    }

    @Listener(order = Order.LAST)
    public void lastEvent(InteractContainerEvent.Close spongeEvent) {
        fireEvent(spongeEvent, EventPriority.LOWEST);
    }


    private void fireEvent(InteractContainerEvent.Close spongeEvent, EventPriority priority) {
        var inventoryView = AbstractInventoryView.wrap(spongeEvent.container());
        InventoryCloseEvent bukkitEvent = new InventoryCloseEvent(inventoryView);

        SoakManager.<WrapperManager>getManager().getServer().getSoakPluginManager().callEvent(this.singleEventListener, bukkitEvent, priority);
    }
}
