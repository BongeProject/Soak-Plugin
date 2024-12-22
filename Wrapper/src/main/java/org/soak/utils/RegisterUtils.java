package org.soak.utils;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.soak.wrapper.inventory.meta.AbstractItemMeta;

public class RegisterUtils {

    public static void registerSerializable() {
        ConfigurationSerialization.registerClass(ItemStack.class);
        ConfigurationSerialization.registerClass(AbstractItemMeta.class, "ItemMeta");

    }
}
