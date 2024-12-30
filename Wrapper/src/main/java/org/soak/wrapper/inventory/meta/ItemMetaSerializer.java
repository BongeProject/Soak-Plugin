package org.soak.wrapper.inventory.meta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.soak.generate.bukkit.MaterialList;
import org.soak.map.item.SoakItemStackMap;
import org.soak.plugin.SoakManager;
import org.soak.plugin.SoakPluginContainer;
import org.soak.utils.GeneralHelper;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.*;
import java.util.stream.Collectors;

public class ItemMetaSerializer {

    public static Map<String, Object> serialize(@NotNull ItemMeta meta) {
        Map<String, Object> values = new HashMap<>();
        values.put("meta-type", meta.getClass().getName());
        values.put("ContentVersion", 3);
        values.put("lore", Objects
                .requireNonNullElse(meta.lore(), Collections.<Component>emptyList())
                .stream()
                .map(c -> LegacyComponentSerializer.legacySection().serialize(c))
                .toList());
        var component = meta.displayName();
        if (component != null) {
            values.put("display-name", GsonComponentSerializer.gson().serialize(component));
        }
        if (meta instanceof AbstractItemMeta itemMeta) {
            values.put("id", itemMeta.container.type().key(RegistryTypes.ITEM_TYPE).formatted());
            values.put("count", itemMeta.quantity());
        }
        return values;
    }


    public static ItemMeta deserialize(Map<String, Object> values) {
        try {
            ItemStack.Builder itemStackBuilder = ItemStack.builder();

            values.forEach((key, value) -> {
                switch (key) {
                    case "id" -> {
                        ItemType type = ItemTypes.registry().findValue(ResourceKey.resolve((String) value)).orElseThrow(() -> new IllegalArgumentException("Unknown ItemType of " + value));
                        itemStackBuilder.itemType(type);
                    }
                    case "type" -> {
                        var enumName = MaterialList.matchMaterial(value.toString());
                        if (enumName == null) {
                            SoakManager.getManager().getLogger().error("Unknown type");
                            return;
                        }
                        itemStackBuilder.itemType(MaterialList.getItemType(enumName).orElseThrow());
                    }
                    case "count" -> itemStackBuilder.quantity((int) value);
                    case "ContentVersion" -> {
                        if (((Integer) value) == 3) {
                            return;
                        }
                        SoakManager.getManager().getLogger().error("Unable to read ContentVersion of " + value + " -> Attempting anyway");
                    }
                    case "lore" -> {
                        List<String> lore = (List<String>) value;
                        List<Component> componentLore = lore.stream().map(l -> (Component) LegacyComponentSerializer.legacySection().deserialize(l)).toList();
                        itemStackBuilder.add(Keys.LORE, componentLore);
                    }
                    case "display-name" -> {
                        String jsonStringComponent = (String) value;
                        Component component;
                        if (jsonStringComponent.startsWith("{")) {
                            try {
                                component = GsonComponentSerializer.gson().deserialize(jsonStringComponent);
                            } catch (Throwable e) {
                                SoakManager.getManager().getLogger().error("Could not read json of " + jsonStringComponent, e);
                                return;
                            }
                        } else {
                            component = LegacyComponentSerializer.legacySection().deserialize(jsonStringComponent);
                        }
                        itemStackBuilder.add(Keys.DISPLAY_NAME, component);

                    }
                    default ->
                            SoakManager.getManager().getLogger().error("Unknown Item Deserialize key of -> " + key + ": (" + value.getClass().getSimpleName() + ") " + value);
                }


            });
            return SoakItemStackMap.toBukkitMeta(itemStackBuilder.build());
        } catch (Throwable e) {
            SoakManager.getManager().displayError(e, SoakManager.getManager().getSoakContainer(GeneralHelper.fromStackTrace()).map(SoakPluginContainer::getBukkitInstance).orElseThrow(), Map.entry("Data", values.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue().toString()).collect(Collectors.joining("\n\t- "))));
            throw new RuntimeException(e);
        }
    }
}
