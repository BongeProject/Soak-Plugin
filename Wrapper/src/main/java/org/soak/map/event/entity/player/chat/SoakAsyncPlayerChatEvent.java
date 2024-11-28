package org.soak.map.event.entity.player.chat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.soak.WrapperManager;
import org.soak.map.SoakMessageMap;
import org.soak.map.event.EventSingleListenerWrapper;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.ExecuteCommandEvent;
import org.spongepowered.api.event.message.PlayerChatEvent;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SoakAsyncPlayerChatEvent {

    private static final String DEFAULT_FORMAT = "<%1$s> %2$s";


    private final EventSingleListenerWrapper<AsyncPlayerChatEvent> singleEventListener;

    public SoakAsyncPlayerChatEvent(EventSingleListenerWrapper<AsyncPlayerChatEvent> singleEventListener) {
        this.singleEventListener = singleEventListener;
    }

    @Listener(order = Order.FIRST)
    public void firstEvent(PlayerChatEvent.Submit spongeEvent) {
        fireEvent(spongeEvent, EventPriority.HIGHEST);
    }

    @Listener(order = Order.EARLY)
    public void earlyEvent(PlayerChatEvent.Submit spongeEvent) {
        fireEvent(spongeEvent, EventPriority.HIGH);
    }

    @Listener(order = Order.DEFAULT)
    public void normalEvent(PlayerChatEvent.Submit spongeEvent) {
        fireEvent(spongeEvent, EventPriority.NORMAL);
    }

    @Listener(order = Order.LATE)
    public void lateEvent(PlayerChatEvent.Submit spongeEvent) {
        fireEvent(spongeEvent, EventPriority.LOW);
    }

    @Listener(order = Order.LAST)
    public void lastEvent(PlayerChatEvent.Submit spongeEvent) {
        fireEvent(spongeEvent, EventPriority.LOWEST);
    }


    private void fireEvent(PlayerChatEvent.Submit event, EventPriority priority) {
        var opPlayer = event.player();
        if (opPlayer.isEmpty()) {
            return;
        }
        var player = opPlayer.get();
        var bukkitPlayer = SoakManager.<WrapperManager>getManager().getMemoryStore().get(player);
        Set<Player> receivers = event
                .filter()
                .map(filter -> Sponge
                        .server()
                        .onlinePlayers()
                        .stream()
                        .filter(filter)
                        .map(spongePlayer -> (Player) SoakManager
                                .<WrapperManager>getManager()
                                .getMemoryStore()
                                .get(spongePlayer))
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
        var message = SoakMessageMap.mapToBukkit(event.message());

        var bukkitEvent = new AsyncPlayerChatEvent(Bukkit.getServer().isPrimaryThread(), bukkitPlayer, message, receivers);
        SoakManager.<WrapperManager>getManager().getServer().getSoakPluginManager().callEvent(this.singleEventListener, bukkitEvent, priority);

        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
        }
        if (!message.equals(bukkitEvent.getMessage())) {
            var displayName = bukkitPlayer.getDisplayName();
            var eventMessage = bukkitEvent.getMessage();
            var formattedMessage = String.format(bukkitEvent.getMessage(), displayName, eventMessage);
            event.setMessage(SoakMessageMap.toComponent(formattedMessage));
        }
    }
}
