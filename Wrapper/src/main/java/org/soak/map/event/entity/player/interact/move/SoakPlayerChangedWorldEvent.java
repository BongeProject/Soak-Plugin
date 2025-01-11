package org.soak.map.event.entity.player.interact.move;

import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.soak.WrapperManager;
import org.soak.map.event.EventSingleListenerWrapper;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;

public class SoakPlayerChangedWorldEvent {

    private final EventSingleListenerWrapper<PlayerChangedWorldEvent> singleEventListener;

    public SoakPlayerChangedWorldEvent(EventSingleListenerWrapper<PlayerChangedWorldEvent> listener) {
        this.singleEventListener = listener;
    }

    @Listener(order = Order.FIRST)
    public void firstEvent(RespawnPlayerEvent.SelectWorld event) {
        fireEvent(event, EventPriority.HIGHEST);
    }

    @Listener(order = Order.EARLY)
    public void earlyEvent(RespawnPlayerEvent.SelectWorld event) {
        fireEvent(event, EventPriority.HIGH);
    }

    @Listener(order = Order.DEFAULT)
    public void normalEvent(RespawnPlayerEvent.SelectWorld event) {
        fireEvent(event, EventPriority.NORMAL);
    }

    @Listener(order = Order.LATE)
    public void lateEvent(RespawnPlayerEvent.SelectWorld event) {
        fireEvent(event, EventPriority.LOW);
    }

    @Listener(order = Order.LAST)
    public void lastEvent(RespawnPlayerEvent.SelectWorld event) {
        fireEvent(event, EventPriority.LOWEST);
    }

    private void fireEvent(RespawnPlayerEvent.SelectWorld event, EventPriority priority) {
        var spongePlayer = event.entity();
        var spongeFromWorld = spongePlayer.world();

        var bukkitPlayer = SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongePlayer);
        var bukkitWorld = SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongeFromWorld);
        PlayerChangedWorldEvent bukkitEvent = new PlayerChangedWorldEvent(bukkitPlayer, bukkitWorld);

        var soakPlugin = singleEventListener.plugin();
        var soakContainer = SoakManager.getManager().getSoakContainer(soakPlugin);
        Sponge.server().scheduler().executor(soakContainer.getTrueContainer()).execute(() -> SoakManager.<WrapperManager>getManager().getServer().getSoakPluginManager().callEvent(singleEventListener, bukkitEvent, priority));
    }
}
