package org.soak.map.event.entity.player.chat;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.soak.WrapperManager;
import org.soak.map.SoakMessageMap;
import org.soak.map.SoakSubjectMap;
import org.soak.map.event.EventSingleListenerWrapper;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.ChatTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.PlayerChatEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SoakAsyncChatEvent {

    private final EventSingleListenerWrapper<AsyncPlayerChatEvent> singleEventListener;

    public SoakAsyncChatEvent(EventSingleListenerWrapper<AsyncPlayerChatEvent> singleEventListener) {
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
        Set<Audience> receivers = new HashSet<>(event
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
                .orElse(Collections.emptySet()));
        if (event.chatType().equals(ChatTypes.CHAT)) {
            receivers.add(SoakSubjectMap.mapToBukkit(Sponge.systemSubject()));
        }
        var message = event.message();
        var originalMessage = event.originalMessage();
        var signedMessage = event.isSigned() ? SignedMessage.system(PlainTextComponentSerializer.plainText().serialize(event.originalMessage()), event.originalMessage()) : null; //TODO ideally get signed message
        var chatRender = ChatRenderer.defaultRenderer(); //TODO find out the alternative

        var bukkitEvent = new AsyncChatEvent(Bukkit.getServer().isPrimaryThread(), bukkitPlayer, receivers, chatRender, message, originalMessage, signedMessage);
        SoakManager.<WrapperManager>getManager().getServer().getSoakPluginManager().callEvent(this.singleEventListener, bukkitEvent, priority);

        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
        }
        if (!message.equals(bukkitEvent.message())) {
            event.setMessage(bukkitEvent.message());
        }
    }
}
