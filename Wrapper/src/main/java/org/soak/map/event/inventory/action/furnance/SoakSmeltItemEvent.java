package org.soak.map.event.inventory.action.furnance;

import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.CookingRecipe;
import org.soak.WrapperManager;
import org.soak.map.event.EventSingleListenerWrapper;
import org.soak.map.item.SoakItemStackMap;
import org.soak.map.item.SoakRecipeMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlock;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.entity.CookingEvent;

public class SoakSmeltItemEvent {

    private final EventSingleListenerWrapper<FurnaceSmeltEvent> singleEventListener;

    public SoakSmeltItemEvent(EventSingleListenerWrapper<FurnaceSmeltEvent> event) {
        this.singleEventListener = event;
    }

    @Listener(order = Order.FIRST)
    public void firstEvent(CookingEvent.Finish spongeEvent) {
        fireEvent(spongeEvent, EventPriority.HIGHEST);
    }

    @Listener(order = Order.EARLY)
    public void earlyEvent(CookingEvent.Finish spongeEvent) {
        fireEvent(spongeEvent, EventPriority.HIGH);
    }

    @Listener(order = Order.DEFAULT)
    public void normalEvent(CookingEvent.Finish spongeEvent) {
        fireEvent(spongeEvent, EventPriority.NORMAL);
    }

    @Listener(order = Order.LATE)
    public void lateEvent(CookingEvent.Finish spongeEvent) {
        fireEvent(spongeEvent, EventPriority.LOW);
    }

    @Listener(order = Order.LAST)
    public void lastEvent(CookingEvent.Finish spongeEvent) {
        fireEvent(spongeEvent, EventPriority.LOWEST);
    }


    private void fireEvent(CookingEvent.Finish event, EventPriority priority) {
        var furnace = new SoakBlock(event.blockEntity()
                .locatableBlock()
                .location()
                .onServer()
                .orElseThrow(() -> new RuntimeException("Location for server could not be created")));
        var original = event.recipe()
                .map(cooking -> cooking.ingredient().displayedItems())
                .filter(items -> items.size() == 1)
                .map(items -> items.get(0))
                .map(
                        SoakItemStackMap::toBukkit)
                .orElseThrow(() -> new RuntimeException("Could not find the before item"));
        var recipe = (CookingRecipe<?>) event.recipe().map(SoakRecipeMap::toBukkit).orElse(null);
        if (event.transactions().size() != 1) {
            throw new RuntimeException("Furnace cooked " + event.transactions().size() + " itemstacks");
        }
        var result = SoakItemStackMap.toBukkit(event.transactions().get(0).finalReplacement());
        var bukkitEvent = new FurnaceSmeltEvent(furnace, original, result, recipe);

        SoakManager.<WrapperManager>getManager().getServer().getSoakPluginManager().callEvent(this.singleEventListener, bukkitEvent, priority);

        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
