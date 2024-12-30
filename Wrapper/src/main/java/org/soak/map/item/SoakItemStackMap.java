package org.soak.map.item;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.generate.bukkit.MaterialList;
import org.soak.utils.SnapshotHelper;
import org.soak.wrapper.inventory.meta.*;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemRarities;
import org.spongepowered.api.item.ItemRarity;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackLike;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Function;

public class SoakItemStackMap {

    public static org.bukkit.inventory.ItemRarity toBukkit(ItemRarity rarity) {
        if (rarity.equals(ItemRarities.COMMON.get())) {
            return org.bukkit.inventory.ItemRarity.COMMON;
        }
        if (rarity.equals(ItemRarities.EPIC.get())) {
            return org.bukkit.inventory.ItemRarity.EPIC;
        }
        if (rarity.equals(ItemRarities.RARE.get())) {
            return org.bukkit.inventory.ItemRarity.RARE;
        }
        return org.bukkit.inventory.ItemRarity.UNCOMMON;
    }

    public static ItemRarity toSponge(org.bukkit.inventory.ItemRarity rarity) {
        return switch (rarity) {
            case COMMON -> ItemRarities.COMMON.get();
            case UNCOMMON -> ItemRarities.UNCOMMON.get();
            case RARE -> ItemRarities.RARE.get();
            case EPIC -> ItemRarities.EPIC.get();
            default -> throw new IllegalArgumentException("Unknown mapping from " + rarity.name());
        };
    }

    public static org.bukkit.inventory.ItemStack toBukkit(@NotNull ItemStack stack) {
        return toBukkit(stack.type(), stack.quantity(), stack);
    }

    public static org.bukkit.inventory.ItemStack toBukkit(@NotNull ItemStackSnapshot stack) {
        return toBukkit(stack.type(), stack.quantity(), stack);
    }

    public static ItemStack toSponge(@NotNull ItemMeta meta) {
        return toSponge(meta,
                AbstractItemMeta::asStack);
    }

    public static ItemStackSnapshot toSpongeSnapshot(@NotNull ItemMeta meta) {
        return toSponge(meta,
                AbstractItemMeta::asSnapshot);
    }

    public static Optional<ItemType> toSponge(@NotNull Material material) {
        return MaterialList.getItemType(material);
    }

    public static Material toBukkit(@NotNull ItemType type) {
        return MaterialList.value(type);
    }

    public static EnumSet<Material> materials() {
        return MaterialList.values();
    }

    public static ItemStack toSponge(@Nullable org.bukkit.inventory.ItemStack stack) {
        if (stack == null) {
            return ItemStack.empty();
        }
        if (!stack.hasItemMeta()) {
            return ItemStack.of(toSponge(stack.getType())
                    .orElseThrow(() -> new RuntimeException("Material of " + stack.getType()
                            .name() + " is not an item")), stack.getAmount());
        }
        return toSponge(stack,
                AbstractItemMeta::asStack);
    }

    public static ItemStackSnapshot toSpongeSnapshot(@NotNull org.bukkit.inventory.ItemStack meta) {
        return toSponge(meta,
                AbstractItemMeta::asSnapshot);
    }

    private static <VC extends ItemStackLike> VC toSponge(@NotNull ItemMeta meta, @NotNull Function<AbstractItemMeta, VC> spongeType) {
        if (!(meta instanceof AbstractItemMeta)) {
            throw new RuntimeException(meta.getClass().getName() + " does not extend AbstractItemMeta");
        }
        return spongeType.apply((AbstractItemMeta) meta);
    }

    private static <VC extends ItemStackLike> VC toSponge(@NotNull org.bukkit.inventory.ItemStack stack, @NotNull Function<AbstractItemMeta, VC> spongeType) {
        if (!stack.hasItemMeta()) {
            throw new RuntimeException(
                    "ItemStack provided does not have any meta. This should be checked for in the previous method");
        }
        VC valueContainer = toSponge(stack.getItemMeta(), spongeType);

        //needs to update the quantity from Bukkit's ItemStack
        if (valueContainer instanceof ItemStack spongeStack) {
            spongeStack.setQuantity(stack.getAmount());
            return (VC) spongeStack;
        }
        ItemStackSnapshot snapshot = (ItemStackSnapshot) valueContainer;
        return (VC) SnapshotHelper.copyWithQuantity(snapshot, stack.getAmount());
    }

    private static org.bukkit.inventory.ItemStack toBukkit(@NotNull ItemType type, int amount, @NotNull ItemStackLike container) {
        var stack = new org.bukkit.inventory.ItemStack(toBukkit(type), amount);
        stack.setItemMeta(toBukkitMeta(container));
        return stack;
    }

    public static AbstractItemMeta toBukkitMeta(ItemStackLike stack, @Nullable Class<? extends ItemMeta> publicClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var attempt = toBukkitMeta(stack);
        if (publicClass == null || publicClass.isInstance(attempt)) {
            return attempt;
        }

        var metaType = new Class<?>[]{SoakPotionItemMeta.class, SoakFireworkEffectMeta.class, SoakSkullMeta.class, SoakLeatherArmorMeta.class};
        var metaClass = Arrays.stream(metaType).filter(clazz -> clazz.isAssignableFrom(publicClass)).findAny().orElseThrow();
        return (AbstractItemMeta) metaClass.getConstructor(ItemStackLike.class).newInstance(stack);
    }

    public static AbstractItemMeta toBukkitMeta(@NotNull ItemStackLike container) {
        ItemType type = container.type();
        if (container.supports(Keys.POTION_EFFECTS)) {
            return new SoakPotionItemMeta(container);
        }
        if (container.supports(Keys.FIREWORK_EFFECTS)) {
            return new SoakFireworkEffectMeta(container);
        }
        if (container.supports(Keys.REPAIR_COST)) {
            return new SoakRepairable(container);
        }
        if (type.equals(ItemTypes.PLAYER_HEAD.get()) || container.supports(Keys.SKIN_PROFILE_PROPERTY)) {
            return new SoakSkullMeta(container);
        }
        if (type.equals(ItemTypes.LEATHER_BOOTS.get()) || type.equals(ItemTypes.LEATHER_CHESTPLATE.get()) || type.equals(ItemTypes.LEATHER_HELMET.get()) || type.equals(ItemTypes.LEATHER_LEGGINGS.get())) {
            return new SoakLeatherArmorMeta(container);
        }
        return new SoakItemMeta(container);
    }
}
