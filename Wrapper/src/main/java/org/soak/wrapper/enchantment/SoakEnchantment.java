package org.soak.wrapper.enchantment;

import io.papermc.paper.enchantments.EnchantmentRarity;
import io.papermc.paper.registry.set.RegistryKeySet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakMessageMap;
import org.soak.map.SoakResourceKeyMap;
import org.soak.map.item.SoakEnchantmentTypeMap;
import org.soak.map.item.SoakItemStackMap;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.Set;

public class SoakEnchantment extends Enchantment {

    private final EnchantmentType type;

    public SoakEnchantment(@NotNull EnchantmentType type) {
        this.type = type;
    }

    @Override
    public @NotNull String getName() {
        return PlainTextComponentSerializer.plainText().serialize(this.type.asComponent());
    }

    @Override
    public int getMaxLevel() {
        return this.type.maximumLevel();
    }

    @Override
    public int getStartLevel() {
        return this.type.minimumLevel();
    }

    @Override
    public boolean isTreasure() {
        return this.type.isTreasure();
    }

    @Override
    public boolean isCursed() {
        return this.type.isCurse();
    }

    @Override
    public @NotNull Component displayName(int level) {
        return this.type.asComponent(); //level makes it different?
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        throw NotImplementedException.createByLazy(Enchantment.class, "getItemTarget");
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment arg0) {
        return !this.type.isCompatibleWith(SoakEnchantmentTypeMap.toSponge(arg0));
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack arg0) {
        var spongeStack = SoakItemStackMap.toSponge(arg0);
        return this.type.canBeAppliedToStack(spongeStack);
    }

    @Override
    public boolean isTradeable() {
        throw NotImplementedException.createByLazy(Enchantment.class, "isTradeable");
    }

    @Override
    public boolean isDiscoverable() {
        throw NotImplementedException.createByLazy(Enchantment.class, "isDiscoverable");
    }

    @Override
    public int getMinModifiedCost(int i) {
        throw NotImplementedException.createByLazy(Enchantment.class, "getMinModifiedCost", int.class);
    }

    @Override
    public int getMaxModifiedCost(int i) {
        throw NotImplementedException.createByLazy(Enchantment.class, "getMaxModifiedCost", int.class);
    }

    @Override
    public int getAnvilCost() {
        throw NotImplementedException.createByLazy(Enchantment.class, "getAnvilCost");
    }

    @Override
    public @NotNull EnchantmentRarity getRarity() {
        throw NotImplementedException.createByLazy(Enchantment.class, "getRarity");
    }

    @Override
    public float getDamageIncrease(int arg0, @NotNull EntityCategory arg1) {
        throw NotImplementedException.createByLazy(Enchantment.class, "getDamageIncrease", int.class, EntityCategory.class);
    }

    @Override
    public float getDamageIncrease(int i, @NotNull EntityType entityType) {
        throw NotImplementedException.createByLazy(Enchantment.class, "getDamageIncrease", int.class, EntityType.class);
    }

    @Override
    public @NotNull Set<EquipmentSlot> getActiveSlots() {
        throw NotImplementedException.createByLazy(Enchantment.class, "getActiveSlots");
    }

    @Override
    public @NotNull Set<EquipmentSlotGroup> getActiveSlotGroups() {
        throw NotImplementedException.createByLazy(Enchantment.class, "getActiveSlotGroups");
    }

    @Override
    public @NotNull Component description() {
        throw NotImplementedException.createByLazy(Enchantment.class, "description");
    }

    @Override
    public @NotNull RegistryKeySet<ItemType> getSupportedItems() {
        throw NotImplementedException.createByLazy(Enchantment.class, "getSupportedItems");
    }

    @Override
    public @Nullable RegistryKeySet<ItemType> getPrimaryItems() {
        throw NotImplementedException.createByLazy(Enchantment.class, "getPrimaryItems");
    }

    @Override
    public int getWeight() {
        return this.type.weight();
    }

    @Override
    public @NotNull RegistryKeySet<Enchantment> getExclusiveWith() {
        throw NotImplementedException.createByLazy(Enchantment.class, "getExclusiveWith");
    }

    @Override
    public @NotNull String translationKey() {
        var component = this.type.asComponent();
        if (!(component instanceof TranslatableComponent translatable)) {
            throw new RuntimeException("No translation key could be found");
        }
        return translatable.key();
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return SoakResourceKeyMap.mapToBukkit(this.type.key(RegistryTypes.ENCHANTMENT_TYPE));
    }

    @Override
    public @NotNull String getTranslationKey() {
        var component = this.type.asComponent();
        if (!(component instanceof TranslatableComponent translationKey)) {
            throw new RuntimeException("Cannot get translation key from Enchantment of " + SoakMessageMap.mapToBukkit(component));
        }
        return translationKey.key();
    }
}
