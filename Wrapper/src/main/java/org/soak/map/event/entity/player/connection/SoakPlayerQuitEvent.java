package org.soak.map.event.entity.player.connection;

import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.soak.WrapperManager;
import org.soak.map.event.EventSingleListenerWrapper;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class SoakPlayerQuitEvent {

    private final EventSingleListenerWrapper<PlayerQuitEvent> singleListenerWrapper;

    public SoakPlayerQuitEvent(EventSingleListenerWrapper<PlayerQuitEvent> singleListenerWrapper) {
        this.singleListenerWrapper = singleListenerWrapper;
    }

    @Listener(order = Order.FIRST)
    public void firstEvent(ServerSideConnectionEvent.Leave spongeEvent) {
        fireEvent(spongeEvent, EventPriority.HIGHEST);
    }

    @Listener(order = Order.EARLY)
    public void earlyEvent(ServerSideConnectionEvent.Leave spongeEvent) {
        fireEvent(spongeEvent, EventPriority.HIGH);
    }

    @Listener(order = Order.DEFAULT)
    public void normalEvent(ServerSideConnectionEvent.Leave spongeEvent) {
        fireEvent(spongeEvent, EventPriority.NORMAL);
    }

    @Listener(order = Order.LATE)
    public void lateEvent(ServerSideConnectionEvent.Leave spongeEvent) {
        fireEvent(spongeEvent, EventPriority.LOW);
    }

    @Listener(order = Order.LAST)
    public void lastEvent(ServerSideConnectionEvent.Leave spongeEvent) {
        fireEvent(spongeEvent, EventPriority.LOWEST);
    }


    private void fireEvent(ServerSideConnectionEvent.Leave event, EventPriority priority) {
        var player = SoakManager.<WrapperManager>getManager().getMemoryStore().get(event.player());
        var message = event.message();

        var bukkitEvent = new PlayerQuitEvent(player, message); //TODO PlayerQuitEvent.QuitReason
        SoakManager.<WrapperManager>getManager().getServer().getSoakPluginManager().callEvent(this.singleListenerWrapper, bukkitEvent, priority);

        var newQuitMessage = bukkitEvent.quitMessage();

        if (newQuitMessage != null && !newQuitMessage.equals(message)) {
            event.setMessage(newQuitMessage);
        }
        if (newQuitMessage == null) {
            event.setAudience(null);
        }
    }

}
