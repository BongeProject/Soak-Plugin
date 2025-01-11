package org.soak.map.event.entity.player.connection;

import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.soak.WrapperManager;
import org.soak.map.event.EventSingleListenerWrapper;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

import java.net.InetAddress;
import java.util.UUID;

public class SoakPreJoinEvent {

    private final EventSingleListenerWrapper<AsyncPlayerPreLoginEvent> singleListenerWrapper;

    public SoakPreJoinEvent(EventSingleListenerWrapper<AsyncPlayerPreLoginEvent> singleListener) {
        this.singleListenerWrapper = singleListener;
    }

    @Listener(order = Order.FIRST)
    public void firstEvent(ServerSideConnectionEvent.Login spongeEvent) {
        fireEvent(spongeEvent, EventPriority.HIGHEST);
    }

    @Listener(order = Order.EARLY)
    public void earlyEvent(ServerSideConnectionEvent.Login spongeEvent) {
        fireEvent(spongeEvent, EventPriority.HIGH);
    }

    @Listener(order = Order.DEFAULT)
    public void normalEvent(ServerSideConnectionEvent.Login spongeEvent) {
        fireEvent(spongeEvent, EventPriority.NORMAL);
    }

    @Listener(order = Order.LATE)
    public void lateEvent(ServerSideConnectionEvent.Login spongeEvent) {
        fireEvent(spongeEvent, EventPriority.LOW);
    }

    @Listener(order = Order.LAST)
    public void lastEvent(ServerSideConnectionEvent.Login spongeEvent) {
        fireEvent(spongeEvent, EventPriority.LOWEST);
    }


    public void fireEvent(ServerSideConnectionEvent.Login event, EventPriority priority) {
        String profileName = event.profile().name().orElse("");
        UUID uuid = event.profile().uniqueId();
        InetAddress address = event.connection().address().getAddress();

        AsyncPlayerPreLoginEvent bukkitEvent = new AsyncPlayerPreLoginEvent(profileName, address, uuid);
        SoakManager.<WrapperManager>getManager().getServer().getSoakPluginManager().callEvent(this.singleListenerWrapper, bukkitEvent, priority);
    }
}
