package org.soak.wrapper.inventory;

import com.google.common.collect.Multimap;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakResourceKeyMap;
import org.soak.map.item.SoakItemStackMap;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.tag.ItemTypeTags;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public class SoakItemTypeTyped<T extends ItemMeta> implements ItemType.Typed<T> {

    private final org.spongepowered.api.item.ItemType type;
    private final @Nullable Class<T> metaClass;

    public SoakItemTypeTyped(org.spongepowered.api.item.ItemType type) {
        this(type, null);
    }

    public SoakItemTypeTyped(org.spongepowered.api.item.ItemType type, @Nullable Class<T> meta) {
        this.type = type;
        this.metaClass = meta;
    }

    @Override
    public @NotNull Typed<ItemMeta> typed() {
        return new SoakItemTypeTyped<>(type, null);
    }

    @Override
    public @NotNull <M extends ItemMeta> Typed<M> typed(@NotNull Class<M> aClass) {
        return new SoakItemTypeTyped<>(type, aClass);
    }

    @Override
    public boolean hasBlockType() {
        return type.block().isPresent();
    }

    @Override
    public @NotNull BlockType getBlockType() {
        throw NotImplementedException.createByLazy(ItemType.class, "getBlockType");
    }

    @Override
    public @NotNull Class<T> getItemMetaClass() {
        return this.metaClass;
    }

    @Override
    public int getMaxStackSize() {
        return this.type.maxStackQuantity();
    }

    @Override
    public short getMaxDurability() {
        return this.type.get(Keys.MAX_DURABILITY).map(Integer::shortValue).orElse((short) 0);
    }

    @Override
    public boolean isEdible() {
        throw NotImplementedException.createByLazy(ItemType.class, "isEdiable");
    }

    @Override
    public boolean isRecord() {
        return this.type.is(ItemTypeTags.CREEPER_DROP_MUSIC_DISCS);
    }

    @Override
    public boolean isFuel() {
        throw NotImplementedException.createByLazy(ItemType.class, "isFuel");
    }

    @Override
    public boolean isCompostable() {
        throw NotImplementedException.createByLazy(ItemType.class, "isCompostable");

    }

    @Override
    public float getCompostChance() {
        throw NotImplementedException.createByLazy(ItemType.class, "getCompostChance");
    }

    @Override
    public @Nullable ItemType getCraftingRemainingItem() {
        throw NotImplementedException.createByLazy(ItemType.class, "getCraftingRemainingItem");
    }

    @Override
    public @NotNull @Unmodifiable Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers() {
        throw NotImplementedException.createByLazy(ItemType.class, "getDefaultAttributeModifiers");
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot equipmentSlot) {
        throw NotImplementedException.createByLazy(ItemType.class, "getDefaultAttributeModifiers", EquipmentSlot.class);

    }

    @Override
    public @Nullable CreativeCategory getCreativeCategory() {
        throw NotImplementedException.createByLazy(ItemType.class, "getCreativeCategory");
    }

    @Override
    public boolean isEnabledByFeature(@NotNull World world) {
        throw NotImplementedException.createByLazy(ItemType.class, "isEnabledByFeature", World.class);
    }

    @Override
    public @Nullable Material asMaterial() {
        return SoakItemStackMap.toBukkit(this.type);
    }

    @Override
    public @NotNull String getTranslationKey() {
        return translationKey();
    }

    @Override
    public @Nullable ItemRarity getItemRarity() {
        throw NotImplementedException.createByLazy(ItemType.class, "ItemRarity");
    }

    @Override
    public @NotNull ItemStack createItemStack() {
        return createItemStack(1);
    }

    @Override
    public @NotNull ItemStack createItemStack(int i) {
        return createItemStack(i, null);
    }

    @Override
    public @NotNull ItemStack createItemStack(@Nullable Consumer<? super T> consumer) {
        return createItemStack(1, consumer);
    }

    @Override
    public @NotNull ItemStack createItemStack(int i, @Nullable Consumer<? super T> consumer) {
        var stack = new ItemStack(Material.AIR, i).withType(this.asMaterial());
        var spongeStack = SoakItemStackMap.toSponge(stack);
        try {
            SoakItemStackMap.toBukkitMeta(spongeStack, this.metaClass);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (consumer != null) {
            consumer.accept((T) stack.getItemMeta());
        }
        return stack;
    }

    @Override
    public @NotNull String translationKey() {
        var component = this.type.asComponent();
        if (!(component instanceof TranslatableComponent translation)) {
            throw new IllegalStateException("Cannot get translation key from item");
        }
        return translation.key();
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return SoakResourceKeyMap.mapToBukkit(this.type.key(RegistryTypes.ITEM_TYPE));
    }
}
