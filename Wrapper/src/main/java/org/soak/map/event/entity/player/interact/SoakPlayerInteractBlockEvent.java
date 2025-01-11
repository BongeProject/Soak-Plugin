package org.soak.map.event.entity.player.interact;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.soak.WrapperManager;
import org.soak.map.event.EventSingleListenerWrapper;
import org.soak.map.SoakActionMap;
import org.soak.map.SoakDirectionMap;
import org.soak.map.item.SoakItemStackMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlockSnapshot;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

public class SoakPlayerInteractBlockEvent {

    private final EventSingleListenerWrapper<PlayerInteractEvent> singleEventListener;

    public SoakPlayerInteractBlockEvent(EventSingleListenerWrapper<PlayerInteractEvent> singleEventListener) {
        this.singleEventListener = singleEventListener;
    }

    @Listener(order = Order.FIRST)
    public void firstEvent(InteractBlockEvent event, @First ServerPlayer player) {
        fireEvent(event, player, EventPriority.HIGHEST);
    }

    @Listener(order = Order.EARLY)
    public void earlyEvent(InteractBlockEvent event, @First ServerPlayer player) {
        fireEvent(event, player, EventPriority.HIGH);
    }

    @Listener(order = Order.DEFAULT)
    public void normalEvent(InteractBlockEvent event, @First ServerPlayer player) {
        fireEvent(event, player, EventPriority.NORMAL);
    }

    @Listener(order = Order.LATE)
    public void lateEvent(InteractBlockEvent event, @First ServerPlayer player) {
        fireEvent(event, player, EventPriority.LOW);
    }

    @Listener(order = Order.LAST)
    public void lastEvent(InteractBlockEvent event, @First ServerPlayer player) {
        fireEvent(event, player, EventPriority.LOWEST);
    }

    private void fireEvent(InteractBlockEvent spongeEvent, ServerPlayer spongePlayer, EventPriority priority) {
        var player = SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongePlayer);
        var spongePosition = spongeEvent.block().position();
        var interactionPoint = new Vector(
                spongePosition.x(),
                spongePosition.y(),
                spongePosition.z());
        var clickedFace = SoakDirectionMap.toBukkit(spongeEvent.targetSide());
        var spongeHand = spongeEvent.context()
                .get(EventContextKeys.USED_HAND)
                .orElseThrow(() -> new RuntimeException("Unknown hand type from event"));
        var action = SoakActionMap.toBukkit(spongeHand, false);
        var hand = SoakActionMap.toBukkit(spongeHand);
        var spongeItem = spongePlayer.itemInHand(spongeHand);
        var item = SoakItemStackMap.toBukkit(spongeItem);
        var block = new SoakBlockSnapshot(spongeEvent.block());

        var bukkitEvent = new PlayerInteractEvent(player, action, item, block, clickedFace, hand, interactionPoint);
        SoakManager.<WrapperManager>getManager().getServer().getSoakPluginManager().callEvent(this.singleEventListener, bukkitEvent, priority);

        if (spongeEvent instanceof Cancellable) {
            if (bukkitEvent.useInteractedBlock() == Event.Result.DENY) {
                ((Cancellable) spongeEvent).setCancelled(true);
            }
        }

    }
}
