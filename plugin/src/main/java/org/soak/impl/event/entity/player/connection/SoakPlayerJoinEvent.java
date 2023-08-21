package org.soak.impl.event.entity.player.connection;

import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.soak.impl.event.EventSingleListenerWrapper;
import org.soak.plugin.SoakPlugin;
import org.soak.wrapper.entity.living.human.SoakPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class SoakPlayerJoinEvent {

    private final EventSingleListenerWrapper<PlayerJoinEvent> singleListenerWrapper;

    public SoakPlayerJoinEvent(EventSingleListenerWrapper<PlayerJoinEvent> singleListenerWrapper) {
        this.singleListenerWrapper = singleListenerWrapper;
    }

    @Listener(order = Order.FIRST)
    public void firstEvent(ServerSideConnectionEvent.Join spongeEvent) {
        fireEvent(spongeEvent, EventPriority.HIGHEST);
    }

    @Listener(order = Order.EARLY)
    public void earlyEvent(ServerSideConnectionEvent.Join spongeEvent) {
        fireEvent(spongeEvent, EventPriority.HIGH);
    }

    @Listener(order = Order.DEFAULT)
    public void normalEvent(ServerSideConnectionEvent.Join spongeEvent) {
        fireEvent(spongeEvent, EventPriority.NORMAL);
    }

    @Listener(order = Order.LATE)
    public void lateEvent(ServerSideConnectionEvent.Join spongeEvent) {
        fireEvent(spongeEvent, EventPriority.LOW);
    }

    @Listener(order = Order.LAST)
    public void lastEvent(ServerSideConnectionEvent.Join spongeEvent) {
        fireEvent(spongeEvent, EventPriority.LOWEST);
    }


    private void fireEvent(ServerSideConnectionEvent.Join event, EventPriority priority) {
        var player = SoakPlugin.plugin().getMemoryStore().get(event.player());
        var message = event.message();

        var bukkitEvent = new PlayerJoinEvent(player, message);
        SoakPlugin.server().getPluginManager().callEvent(this.singleListenerWrapper, bukkitEvent, priority);

        var newJoinMessage = bukkitEvent.joinMessage();

        if (newJoinMessage != null && !newJoinMessage.equals(message)) {
            event.setMessage(newJoinMessage);
        }
        if(newJoinMessage == null){
            event.setMessageCancelled(true);
        }
    }

}
