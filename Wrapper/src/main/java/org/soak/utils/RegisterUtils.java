package org.soak.utils;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.soak.wrapper.inventory.meta.*;

public class RegisterUtils {

    public static void registerSerializable() {
        ConfigurationSerialization.registerClass(AbstractItemMeta.class, "ItemMeta");
        ConfigurationSerialization.registerClass(SoakRepairable.class);
        ConfigurationSerialization.registerClass(FireworkEffectMeta.class);
        ConfigurationSerialization.registerClass(FireworkMeta.class);
        ConfigurationSerialization.registerClass(SoakLeatherArmorMeta.class);
        ConfigurationSerialization.registerClass(SoakPotionItemMeta.class);
        ConfigurationSerialization.registerClass(SoakSkullMeta.class);

    }
}
