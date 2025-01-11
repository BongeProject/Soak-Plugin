package org.bukkit.craftbukkit.v1_21_R1.inventory;

import io.papermc.paper.inventory.ItemRarity;
import io.papermc.paper.inventory.tooltip.TooltipContext;
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
import org.soak.wrapper.inventory.SoakItemStack;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Wrapper for NMSBounce Itemstack ... a wrapper for SoakItemStack .... a wrapper for Sponge Itemstack
 * <p>
 * this is why plugins shouldnt use NMS
 */
public class CraftItemStack extends ItemStack {

    private @NotNull net.minecraft.world.item.ItemStack handle;

    public CraftItemStack(@NotNull net.minecraft.world.item.ItemStack handle) {
        this.handle = handle;
    }

    public CraftItemStack(){
        this(new net.minecraft.world.item.ItemStack(new SoakItemStack()));
    }

    @Override
    public @NotNull Material getType() {
        return this.handle.getSoak().getType();
    }

    @Override
    public void setType(@NotNull Material type) {
        this.handle.getSoak().setType(type);
    }

    @Override
    public @NotNull ItemStack withType(@NotNull Material type) {
        return this.handle.getSoak().withType(type);
    }

    @Override
    public int getAmount() {
        return this.handle.getSoak().getAmount();
    }

    @Override
    public void setAmount(int amount) {
        this.handle.getSoak().setAmount(amount);
    }

    @Override
    @Deprecated
    public @Nullable MaterialData getData() {
        return this.handle.getSoak().getData();
    }

    @Override
    public void setData(@Nullable MaterialData data) {
        this.handle.getSoak().setData(data);
    }

    @Override
    public short getDurability() {
        return this.handle.getSoak().getDurability();
    }

    @Override
    public void setDurability(short durability) {
        this.handle.getSoak().setDurability(durability);
    }

    @Override
    public int getMaxStackSize() {
        return this.handle.getSoak().getMaxStackSize();
    }

    @Override
    public String toString() {
        return this.handle.getSoak().toString();
    }

    @Override
    public boolean equals(Object obj) {
        return this.handle.getSoak().equals(obj);
    }

    @Override
    public boolean isSimilar(@Nullable ItemStack stack) {
        return this.handle.getSoak().isSimilar(stack);
    }

    @Override
    public @NotNull ItemStack clone() {
        return this.handle.getSoak().clone();
    }

    @Override
    public int hashCode() {
        return this.handle.getSoak().hashCode();
    }

    @Override
    public boolean containsEnchantment(@NotNull Enchantment ench) {
        return this.handle.getSoak().containsEnchantment(ench);
    }

    @Override
    public int getEnchantmentLevel(@NotNull Enchantment ench) {
        return this.handle.getSoak().getEnchantmentLevel(ench);
    }

    @Override
    public @NotNull Map<Enchantment, Integer> getEnchantments() {
        return this.handle.getSoak().getEnchantments();
    }

    @Override
    public void addEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        this.handle.getSoak().addEnchantments(enchantments);
    }

    @Override
    public void addEnchantment(@NotNull Enchantment ench, int level) {
        this.handle.getSoak().addEnchantment(ench, level);
    }

    @Override
    public void addUnsafeEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        this.handle.getSoak().addUnsafeEnchantments(enchantments);
    }

    @Override
    public void addUnsafeEnchantment(@NotNull Enchantment ench, int level) {
        this.handle.getSoak().addUnsafeEnchantment(ench, level);
    }

    @Override
    public int removeEnchantment(@NotNull Enchantment ench) {
        return this.handle.getSoak().removeEnchantment(ench);
    }

    @Override
    public void removeEnchantments() {
        this.handle.getSoak().removeEnchantments();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return this.handle.getSoak().serialize();
    }

    @Override
    public boolean editMeta(@NotNull Consumer<? super ItemMeta> consumer) {
        return this.handle.getSoak().editMeta(consumer);
    }

    @Override
    public <M extends ItemMeta> boolean editMeta(@NotNull Class<M> metaClass, @NotNull Consumer<? super M> consumer) {
        return this.handle.getSoak().editMeta(metaClass, consumer);
    }

    @Override
    public ItemMeta getItemMeta() {
        return this.handle.getSoak().getItemMeta();
    }

    @Override
    public boolean hasItemMeta() {
        return this.handle.getSoak().hasItemMeta();
    }

    @Override
    public boolean setItemMeta(@Nullable ItemMeta itemMeta) {
        return this.handle.getSoak().setItemMeta(itemMeta);
    }

    @Override
    public @NotNull String getTranslationKey() {
        return this.handle.getSoak().getTranslationKey();
    }

    @Override
    public @NotNull ItemStack enchantWithLevels(@Range(from = 1L, to = 30L) int levels, boolean allowTreasure, @NotNull Random random) {
        return this.handle.getSoak().enchantWithLevels(levels, allowTreasure, random);
    }

    @Override
    public @NotNull ItemStack enchantWithLevels(@Range(from = 1L, to = 30L) int levels, @NotNull RegistryKeySet<@NotNull Enchantment> keySet, @NotNull Random random) {
        return this.handle.getSoak().enchantWithLevels(levels, keySet, random);
    }

    @Override
    public @NotNull HoverEvent<HoverEvent.ShowItem> asHoverEvent(@NotNull UnaryOperator<HoverEvent.ShowItem> op) {
        return this.handle.getSoak().asHoverEvent(op);
    }

    @Override
    public @NotNull Component displayName() {
        return this.handle.getSoak().displayName();
    }

    @Override
    public @NotNull ItemStack ensureServerConversions() {
        return this.handle.getSoak().ensureServerConversions();
    }

    @Override
    public @NotNull byte[] serializeAsBytes() {
        return this.handle.getSoak().serializeAsBytes();
    }

    @Override
    public @Nullable String getI18NDisplayName() {
        return this.handle.getSoak().getI18NDisplayName();
    }

    @Override
    public int getMaxItemUseDuration() {
        return this.handle.getSoak().getMaxItemUseDuration();
    }

    @Override
    public int getMaxItemUseDuration(@NotNull LivingEntity entity) {
        return this.handle.getSoak().getMaxItemUseDuration(entity);
    }

    @Override
    public @NotNull ItemStack asOne() {
        return this.handle.getSoak().asOne();
    }

    @Override
    public @NotNull ItemStack asQuantity(int qty) {
        return this.handle.getSoak().asQuantity(qty);
    }

    @Override
    public @NotNull ItemStack add() {
        return this.handle.getSoak().add();
    }

    @Override
    public @NotNull ItemStack add(int qty) {
        return this.handle.getSoak().add(qty);
    }

    @Override
    public @NotNull ItemStack subtract() {
        return this.handle.getSoak().subtract();
    }

    @Override
    public @NotNull ItemStack subtract(int qty) {
        return this.handle.getSoak().subtract(qty);
    }

    @Override
    public @Nullable List<String> getLore() {
        return this.handle.getSoak().getLore();
    }

    @Override
    public void setLore(@Nullable List<String> lore) {
        this.handle.getSoak().setLore(lore);
    }

    @Override
    public @Nullable List<Component> lore() {
        return this.handle.getSoak().lore();
    }

    @Override
    public void lore(@Nullable List<? extends Component> lore) {
        this.handle.getSoak().lore(lore);
    }

    @Override
    public void addItemFlags(@NotNull ItemFlag... itemFlags) {
        this.handle.getSoak().addItemFlags(itemFlags);
    }

    @Override
    public void removeItemFlags(@NotNull ItemFlag... itemFlags) {
        this.handle.getSoak().removeItemFlags(itemFlags);
    }

    @Override
    public @NotNull Set<ItemFlag> getItemFlags() {
        return this.handle.getSoak().getItemFlags();
    }

    @Override
    public boolean hasItemFlag(@NotNull ItemFlag flag) {
        return this.handle.getSoak().hasItemFlag(flag);
    }

    @Override
    public @NotNull String translationKey() {
        return this.handle.getSoak().translationKey();
    }

    @Override
    public @NotNull ItemRarity getRarity() {
        return this.handle.getSoak().getRarity();
    }

    @Override
    public boolean isRepairableBy(@NotNull ItemStack repairMaterial) {
        return this.handle.getSoak().isRepairableBy(repairMaterial);
    }

    @Override
    public boolean canRepair(@NotNull ItemStack toBeRepaired) {
        return this.handle.getSoak().canRepair(toBeRepaired);
    }

    @Override
    public @NotNull ItemStack damage(int amount, @NotNull LivingEntity livingEntity) {
        return this.handle.getSoak().damage(amount, livingEntity);
    }

    @Override
    public boolean isEmpty() {
        return this.handle.getSoak().isEmpty();
    }

    @Override
    public @NotNull @Unmodifiable List<Component> computeTooltipLines(@NotNull TooltipContext tooltipContext, @Nullable Player player) {
        return this.handle.getSoak().computeTooltipLines(tooltipContext, player);
    }

    @Override
    public @NotNull HoverEvent<HoverEvent.ShowItem> asHoverEvent() {
        return this.handle.getSoak().asHoverEvent();
    }
}
