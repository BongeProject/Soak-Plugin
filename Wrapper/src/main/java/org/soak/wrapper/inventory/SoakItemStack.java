package org.soak.wrapper.inventory;

import io.papermc.paper.inventory.ItemRarity;
import io.papermc.paper.inventory.tooltip.TooltipContext;
import io.papermc.paper.persistence.PersistentDataContainerView;
import io.papermc.paper.registry.set.RegistryKeySet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;
import org.soak.map.item.SoakItemStackMap;
import org.soak.wrapper.inventory.meta.AbstractItemMeta;
import org.soak.wrapper.inventory.meta.SoakItemMeta;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class SoakItemStack extends ItemStack {

    public SoakItemStack() {
        this(new SoakItemMeta(ItemStackSnapshot.empty()));
    }

    public SoakItemStack(ItemMeta meta) {
        setItemMeta(meta);
    }

    @Override
    public @NotNull PersistentDataContainerView getPersistentDataContainer() {
        return super.getPersistentDataContainer();
    }

    @Override
    public @NotNull Material getType() {
        return SoakItemStackMap.toBukkit(getItemMeta().sponge().type());
    }

    @Override
    public void setType(@NotNull Material type) {
withType(type);
    }

    @Override
    public @NotNull ItemStack withType(@NotNull Material type) {
        var currentMeta = getItemMeta();
        var itemType = SoakItemStackMap.toSponge(type).orElseThrow(() -> new RuntimeException("material is not item"));
        var itemStack = org.spongepowered.api.item.inventory.ItemStack.of(itemType, currentMeta.quantity());
        var newMeta = SoakItemStackMap.toBukkitMeta(itemStack);
        currentMeta.copyInto(newMeta);
        setItemMeta(newMeta);
        return this;
    }

    @Override
    public int getAmount() {
        return getItemMeta().quantity();
    }

    @Override
    public void setAmount(int amount) {
        super.setAmount(amount);
    }

    @Override
    public @Nullable MaterialData getData() {
        return super.getData();
    }

    @Override
    public void setData(@Nullable MaterialData data) {
        super.setData(data);
    }

    @Override
    public short getDurability() {
        return super.getDurability();
    }

    @Override
    public void setDurability(short durability) {
        super.setDurability(durability);
    }

    @Override
    public int getMaxStackSize() {
        return super.getMaxStackSize();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public boolean isSimilar(@Nullable ItemStack stack) {
        return super.isSimilar(stack);
    }

    @Override
    public @NotNull ItemStack clone() {
        return super.clone();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean containsEnchantment(@NotNull Enchantment ench) {
        return super.containsEnchantment(ench);
    }

    @Override
    public int getEnchantmentLevel(@NotNull Enchantment ench) {
        return super.getEnchantmentLevel(ench);
    }

    @Override
    public @NotNull Map<Enchantment, Integer> getEnchantments() {
        return super.getEnchantments();
    }

    @Override
    public void addEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        super.addEnchantments(enchantments);
    }

    @Override
    public void addEnchantment(@NotNull Enchantment ench, int level) {
        super.addEnchantment(ench, level);
    }

    @Override
    public void addUnsafeEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        super.addUnsafeEnchantments(enchantments);
    }

    @Override
    public void addUnsafeEnchantment(@NotNull Enchantment ench, int level) {
        super.addUnsafeEnchantment(ench, level);
    }

    @Override
    public int removeEnchantment(@NotNull Enchantment ench) {
        return super.removeEnchantment(ench);
    }

    @Override
    public void removeEnchantments() {
        super.removeEnchantments();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return super.serialize();
    }

    @Override
    public boolean editMeta(@NotNull Consumer<? super ItemMeta> consumer) {
        return super.editMeta(consumer);
    }

    @Override
    public <M extends ItemMeta> boolean editMeta(@NotNull Class<M> metaClass, @NotNull Consumer<? super M> consumer) {
        return super.editMeta(metaClass, consumer);
    }

    @Override
    public AbstractItemMeta getItemMeta() {
        return (AbstractItemMeta) super.getItemMeta();
    }

    @Override
    public @NotNull String getTranslationKey() {
        return super.getTranslationKey();
    }

    @Override
    public @NotNull ItemStack enchantWithLevels(@Range(from = 1L, to = 30L) int levels, boolean allowTreasure, @NotNull Random random) {
        return super.enchantWithLevels(levels, allowTreasure, random);
    }

    @Override
    public @NotNull ItemStack enchantWithLevels(@Range(from = 1L, to = 30L) int levels, @NotNull RegistryKeySet<@NotNull Enchantment> keySet, @NotNull Random random) {
        return super.enchantWithLevels(levels, keySet, random);
    }

    @Override
    public @NotNull HoverEvent<HoverEvent.ShowItem> asHoverEvent(@NotNull UnaryOperator<HoverEvent.ShowItem> op) {
        return super.asHoverEvent(op);
    }

    @Override
    public @NotNull Component displayName() {
        return super.displayName();
    }

    @Override
    public @NotNull ItemStack ensureServerConversions() {
        return super.ensureServerConversions();
    }

    @Override
    public @NotNull byte[] serializeAsBytes() {
        return super.serializeAsBytes();
    }

    @Override
    public @Nullable String getI18NDisplayName() {
        return super.getI18NDisplayName();
    }

    @Override
    public int getMaxItemUseDuration() {
        return super.getMaxItemUseDuration();
    }

    @Override
    public int getMaxItemUseDuration(@NotNull LivingEntity entity) {
        return super.getMaxItemUseDuration(entity);
    }

    @Override
    public @NotNull ItemStack asOne() {
        return super.asOne();
    }

    @Override
    public @NotNull ItemStack asQuantity(int qty) {
        return super.asQuantity(qty);
    }

    @Override
    public @NotNull ItemStack add() {
        return super.add();
    }

    @Override
    public @NotNull ItemStack add(int qty) {
        return super.add(qty);
    }

    @Override
    public @NotNull ItemStack subtract() {
        return super.subtract();
    }

    @Override
    public @NotNull ItemStack subtract(int qty) {
        return super.subtract(qty);
    }

    @Override
    public @Nullable List<String> getLore() {
        return super.getLore();
    }

    @Override
    public void setLore(@Nullable List<String> lore) {
        super.setLore(lore);
    }

    @Override
    public @Nullable List<Component> lore() {
        return super.lore();
    }

    @Override
    public void lore(@Nullable List<? extends Component> lore) {
        super.lore(lore);
    }

    @Override
    public void addItemFlags(@NotNull ItemFlag... itemFlags) {
        super.addItemFlags(itemFlags);
    }

    @Override
    public void removeItemFlags(@NotNull ItemFlag... itemFlags) {
        super.removeItemFlags(itemFlags);
    }

    @Override
    public @NotNull Set<ItemFlag> getItemFlags() {
        return super.getItemFlags();
    }

    @Override
    public boolean hasItemFlag(@NotNull ItemFlag flag) {
        return super.hasItemFlag(flag);
    }

    @Override
    public @NotNull String translationKey() {
        return super.translationKey();
    }

    @Override
    public @NotNull ItemRarity getRarity() {
        return super.getRarity();
    }

    @Override
    public boolean isRepairableBy(@NotNull ItemStack repairMaterial) {
        return super.isRepairableBy(repairMaterial);
    }

    @Override
    public boolean canRepair(@NotNull ItemStack toBeRepaired) {
        return super.canRepair(toBeRepaired);
    }

    @Override
    public @NotNull ItemStack damage(int amount, @NotNull LivingEntity livingEntity) {
        return super.damage(amount, livingEntity);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public @NotNull @Unmodifiable List<Component> computeTooltipLines(@NotNull TooltipContext tooltipContext, @Nullable Player player) {
        return super.computeTooltipLines(tooltipContext, player);
    }

    @Override
    public @NotNull HoverEvent<HoverEvent.ShowItem> asHoverEvent() {
        return super.asHoverEvent();
    }
}
