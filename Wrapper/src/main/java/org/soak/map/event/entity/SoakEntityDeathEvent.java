package org.soak.map.event.entity;

import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.soak.WrapperManager;
import org.soak.map.event.EventSingleListenerWrapper;
import org.soak.map.item.SoakItemStackMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.damage.SoakDamageSource;
import org.soak.wrapper.entity.AbstractEntity;
import org.soak.wrapper.entity.living.AbstractLivingEntity;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.server.ServerWorld;

import java.util.stream.Collectors;

public class SoakEntityDeathEvent {
    private final EventSingleListenerWrapper<EntityDeathEvent> singleListenerWrapper;

    public SoakEntityDeathEvent(EventSingleListenerWrapper<EntityDeathEvent> singleListenerWrapper) {
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

    private void fireEvent(DropItemEvent.Destruct event, EventPriority priority) {
        var root = event.cause().root();
        if (!(root instanceof Living)) {
            return;
        }
        if (root instanceof ServerPlayer) {
            //bukkit has a different event for this
            return;
        }
        var entity = (AbstractLivingEntity<?>) AbstractEntity.wrap((Living) root);
        var opSpongeDamageCause = event.cause().first(DamageSource.class);
        if (opSpongeDamageCause.isEmpty()) {
            return;
        }
        var bukkitDamageCause = new SoakDamageSource(opSpongeDamageCause.get(), (ServerWorld) ((Living) root).world());
        var items = event.entities()
                .parallelStream()
                .map(itemEntity -> itemEntity.get(Keys.ITEM_STACK_SNAPSHOT)
                        .orElseThrow(() -> new RuntimeException("Item (" + itemEntity.type()
                                .key(RegistryTypes.ENTITY_TYPE) + ") does not contain an ItemStack")))
                .map(SoakItemStackMap::toBukkit)
                .collect(Collectors.toList());
        //TODO -> find exp
        var bukkitEvent = new EntityDeathEvent(entity, bukkitDamageCause, items);
        SoakManager.<WrapperManager>getManager().getServer().getSoakPluginManager().callEvent(this.singleListenerWrapper, bukkitEvent, priority);

        //TODO -> spawn the entity back in if event is cancelled
        //TODO -> cancel death sounds .... that sounds like a hassle


    }

}
