package org.soak.map.event.entity;

import org.bukkit.entity.Creature;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.soak.WrapperManager;
import org.soak.map.SoakSpawnReasonMap;
import org.soak.map.event.EventSingleListenerWrapper;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.entity.AbstractEntity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.SpawnType;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;

public class SoakCreatureSpawnEvent {

    private final EventSingleListenerWrapper<CreatureSpawnEvent> singleListenerWrapper;

    public SoakCreatureSpawnEvent(EventSingleListenerWrapper<CreatureSpawnEvent> singleListenerWrapper) {
        this.singleListenerWrapper = singleListenerWrapper;
    }

    @Listener(order = Order.FIRST)
    public void firstEvent(SpawnEntityEvent event, @First SpawnType type) {
        fireEvent(event, type, EventPriority.HIGHEST);
    }

    @Listener(order = Order.EARLY)
    public void earlyEvent(SpawnEntityEvent event, @First SpawnType type) {
        fireEvent(event, type, EventPriority.HIGH);
    }

    @Listener(order = Order.DEFAULT)
    public void normalEvent(SpawnEntityEvent event, @First SpawnType type) {
        fireEvent(event, type, EventPriority.NORMAL);
    }

    @Listener(order = Order.LATE)
    public void lateEvent(SpawnEntityEvent event, @First SpawnType type) {
        fireEvent(event, type, EventPriority.LOW);
    }

    @Listener(order = Order.LAST)
    public void lastEvent(SpawnEntityEvent event, @First SpawnType type) {
        fireEvent(event, type, EventPriority.LOWEST);
    }


    private void fireEvent(SpawnEntityEvent event, SpawnType type, EventPriority priority) {
        var reason = SoakSpawnReasonMap.toBukkit(type, event.cause());

        event.filterEntities(entity -> {
            if (!(entity instanceof org.spongepowered.api.entity.living.PathfinderAgent)) {
                return true;
            }
            Creature bukkitEntity = (Creature) AbstractEntity.wrap(entity);
            var bukkitEvent = new CreatureSpawnEvent(bukkitEntity, reason);

            SoakManager.<WrapperManager>getManager().getServer().getSoakPluginManager().callEvent(this.singleListenerWrapper, bukkitEvent, priority);
            return !bukkitEvent.isCancelled();
        });
    }
}
