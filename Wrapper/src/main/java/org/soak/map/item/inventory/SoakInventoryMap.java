package org.soak.map.item.inventory;

import org.bukkit.event.inventory.InventoryType;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ContainerType;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.Slot;

public class SoakInventoryMap {

    public static InventoryType toBukkit(Container container) {
        System.err.println("InventoryType requires ByteBuddy attention");
        return InventoryType.CHEST;
    }

    public static InventoryType.SlotType toBukkit(Slot slot) {
        System.err.println("SlotType requires ByteBuddy attention");
        return InventoryType.SlotType.RESULT;
    }
}
