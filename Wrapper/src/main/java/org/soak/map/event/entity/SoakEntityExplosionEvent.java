package org.soak.map.event.entity;

import org.bukkit.ExplosionResult;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.soak.WrapperManager;
import org.soak.map.event.EventSingleListenerWrapper;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlock;
import org.soak.wrapper.entity.AbstractEntity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.world.ExplosionEvent;

import java.util.stream.Collectors;

public class SoakEntityExplosionEvent {

    private final EventSingleListenerWrapper<EntityExplodeEvent> singleListenerWrapper;

    public SoakEntityExplosionEvent(EventSingleListenerWrapper<EntityExplodeEvent> event) {
        this.singleListenerWrapper = event;
    }

    @Listener(order = Order.FIRST)
    public void firstEvent(ExplosionEvent.Detonate spongeEvent) {
        fireEvent(spongeEvent, EventPriority.HIGHEST);
    }

    @Listener(order = Order.EARLY)
    public void earlyEvent(ExplosionEvent.Detonate spongeEvent) {
        fireEvent(spongeEvent, EventPriority.HIGH);
    }

    @Listener(order = Order.DEFAULT)
    public void normalEvent(ExplosionEvent.Detonate spongeEvent) {
        fireEvent(spongeEvent, EventPriority.NORMAL);
    }

    @Listener(order = Order.LATE)
    public void lateEvent(ExplosionEvent.Detonate spongeEvent) {
        fireEvent(spongeEvent, EventPriority.LOW);
    }

    @Listener(order = Order.LAST)
    public void lastEvent(ExplosionEvent.Detonate spongeEvent) {
        fireEvent(spongeEvent, EventPriority.LOWEST);
    }

    private void fireEvent(ExplosionEvent.Detonate event, EventPriority priority) {
        var opSource = event.explosion().sourceExplosive();
        if (opSource.isEmpty()) {
            return;
        }
        var bukkitEntity = AbstractEntity.wrap(opSource.get());
        var spongeLocation = event.explosion().serverLocation();
        var bukkitWorld = SoakManager.<WrapperManager>getManager().getMemoryStore().get(event.world());
        var bukkitLocation = new Location(bukkitWorld, spongeLocation.x(), spongeLocation.y(), spongeLocation.z());
        var blocks = event.affectedLocations().stream().map(loc -> (Block) new SoakBlock(loc)).collect(Collectors.toList());
        var result = result(event);

        var bukkitEvent = new EntityExplodeEvent(bukkitEntity, bukkitLocation, blocks, 0, result);
        SoakManager.<WrapperManager>getManager().getServer().getSoakPluginManager().callEvent(this.singleListenerWrapper, bukkitEvent, priority);

    }

    private ExplosionResult result(ExplosionEvent.Detonate event) {
        if (!event.explosion().shouldBreakBlocks()) {
            return ExplosionResult.KEEP;
        }
        if (event.explosion().randomness() == 0) {
            return ExplosionResult.DESTROY;
        }
        return ExplosionResult.DESTROY_WITH_DECAY;
    }

}
