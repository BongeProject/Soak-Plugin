package org.soak.impl.event.entity.player.combat;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.soak.impl.event.EventSingleListenerWrapper;
import org.soak.map.item.SoakItemStackMap;
import org.soak.plugin.SoakPlugin;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.registry.RegistryTypes;

public class SoakPlayerDeathEvent {
    private final EventSingleListenerWrapper<PlayerDeathEvent> singleListenerWrapper;
    private Component deathMessage = Component.empty();

    public SoakPlayerDeathEvent(EventSingleListenerWrapper<PlayerDeathEvent> singleListenerWrapper) {
        this.singleListenerWrapper = singleListenerWrapper;
    }

    @Listener(order = Order.FIRST)
    public void firstEvent(DropItemEvent.Destruct spongeEvent) {
        fireEvent(spongeEvent, EventPriority.HIGHEST);
    }

    @Listener(order = Order.EARLY)
    public void earlyEvent(DropItemEvent.Destruct spongeEvent) {
        fireEvent(spongeEvent, EventPriority.HIGH);
    }

    @Listener(order = Order.DEFAULT)
    public void normalEvent(DropItemEvent.Destruct spongeEvent) {
        fireEvent(spongeEvent, EventPriority.NORMAL);
    }

    @Listener(order = Order.LATE)
    public void lateEvent(DropItemEvent.Destruct spongeEvent) {
        fireEvent(spongeEvent, EventPriority.LOW);
    }

    @Listener(order = Order.LAST)
    public void lastEvent(DropItemEvent.Destruct spongeEvent) {
        fireEvent(spongeEvent, EventPriority.LOWEST);
    }

    @Listener
    public void messageEvent(DestructEntityEvent.Death event) {
        this.deathMessage = event.message();
    }

    private void fireEvent(DropItemEvent.Destruct event, EventPriority priority) {
        var root = event.cause().root();
        if (!(root instanceof ServerPlayer spongeEntity)) {
            return;
        }
        var entity = SoakPlugin.plugin().getMemoryStore().get(spongeEntity);
        var items = event.entities()
                .parallelStream()
                .map(itemEntity -> itemEntity.get(Keys.ITEM_STACK_SNAPSHOT)
                        .orElseThrow(() -> new RuntimeException("Item (" + itemEntity.type()
                                .key(RegistryTypes.ENTITY_TYPE) + ") does not contain an ItemStack")))
                .map(SoakItemStackMap::toBukkit)
                .toList();
        //TODO -> find exp
        var bukkitEvent = new PlayerDeathEvent(entity, items, 0, this.deathMessage);
        SoakPlugin.server().getPluginManager().callEvent(this.singleListenerWrapper, bukkitEvent, priority);

        //TODO -> spawn the player back in if event is cancelled
        //TODO -> cancel/change the message
        //TODO -> set should drop experience


    }

}
