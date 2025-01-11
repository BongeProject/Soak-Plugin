package org.soak.map.event.inventory.action;

import org.bukkit.event.EventPriority;
import org.soak.WrapperManager;
import org.soak.map.event.EventSingleListenerWrapper;
import org.soak.map.item.SoakEnchantmentTypeMap;
import org.soak.map.item.SoakItemStackMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.SoakBlock;
import org.soak.wrapper.inventory.view.AbstractInventoryView;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.EnchantItemEvent;
import org.spongepowered.api.item.enchantment.Enchantment;

import java.util.stream.Collectors;

public class SoakEnchantEvent {

    private final EventSingleListenerWrapper<org.bukkit.event.enchantment.EnchantItemEvent> singleEventListener;

    public SoakEnchantEvent(EventSingleListenerWrapper<org.bukkit.event.enchantment.EnchantItemEvent> wrapper) {
        this.singleEventListener = wrapper;
    }

    @Listener(order = Order.FIRST)
    public void firstEvent(EnchantItemEvent.CalculateEnchantment spongeEvent, @First ServerPlayer player) {
        fireEvent(spongeEvent, player, EventPriority.HIGHEST);
    }

    @Listener(order = Order.EARLY)
    public void earlyEvent(EnchantItemEvent.CalculateEnchantment spongeEvent, @First ServerPlayer player) {
        fireEvent(spongeEvent, player, EventPriority.HIGH);
    }

    @Listener(order = Order.DEFAULT)
    public void normalEvent(EnchantItemEvent.CalculateEnchantment spongeEvent, @First ServerPlayer player) {
        fireEvent(spongeEvent, player, EventPriority.NORMAL);
    }

    @Listener(order = Order.LATE)
    public void lateEvent(EnchantItemEvent.CalculateEnchantment spongeEvent, @First ServerPlayer player) {
        fireEvent(spongeEvent, player, EventPriority.LOW);
    }

    @Listener(order = Order.LAST)
    public void lastEvent(EnchantItemEvent.CalculateEnchantment spongeEvent, @First ServerPlayer player) {
        fireEvent(spongeEvent, player, EventPriority.LOWEST);
    }


    private void fireEvent(EnchantItemEvent.CalculateEnchantment event, ServerPlayer serverPlayer, EventPriority priority) {
        var player = SoakManager.<WrapperManager>getManager().getMemoryStore().get(serverPlayer);
        var view = AbstractInventoryView.wrap(event.container());
        SoakBlock enchantmentTable = event.context()
                .get(EventContextKeys.BLOCK_EVENT_PROCESS)
                .flatMap(lb -> lb.location().onServer())
                .map(
                        SoakBlock::new)
                .orElseThrow(() -> new RuntimeException("Could not get the enchantment block"));
        var item = SoakItemStackMap.toBukkit(event.item());
        var enchantments = event.enchantments()
                .parallelStream()
                .collect(Collectors.toMap(spongeEnch -> SoakEnchantmentTypeMap.toBukkit(spongeEnch.type()),
                        Enchantment::level));

        var bukkitEvent = new org.bukkit.event.enchantment.EnchantItemEvent(player,
                view,
                enchantmentTable,
                item,
                event.levelRequirement(),
                enchantments,
                org.bukkit.enchantments.Enchantment.WIND_BURST, //TODO correct this
                1, //TODO correct this
                event.option());
        SoakManager.<WrapperManager>getManager().getServer().getSoakPluginManager().callEvent(this.singleEventListener, bukkitEvent, priority);

        if (bukkitEvent.isCancelled()) {
            event.setCancelled(true);
        }

    }
}
