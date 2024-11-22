package org.soak.wrapper.inventory;

import io.papermc.paper.inventory.ItemRarity;
import io.papermc.paper.inventory.tooltip.TooltipContext;
import io.papermc.paper.persistence.PersistentDataContainerView;
import io.papermc.paper.registry.set.RegistryKeySet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
import org.mose.collection.stream.builder.CollectionStreamBuilder;
import org.mose.collection.stream.specific.list.ListStream;
import org.soak.exception.NotImplementedException;
import org.soak.map.item.SoakItemStackMap;
import org.soak.utils.ListMappingUtils;
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

    private @NotNull AbstractItemMeta meta;

    public SoakItemStack() {
        this(new SoakItemMeta(ItemStackSnapshot.empty()));
    }

    public SoakItemStack(@NotNull AbstractItemMeta meta) {
        this.meta = meta;
    }

    @Override
    public boolean hasItemMeta() {
        return true;
    }

    @Override
    public boolean setItemMeta(@Nullable ItemMeta itemMeta) {
        if (itemMeta == null) {
            var currentStack = this.meta.sponge();
            var newSpongeStack = org.spongepowered.api.item.inventory.ItemStack.of(currentStack.type(), currentStack.quantity());
            this.meta = SoakItemStackMap.toBukkitMeta(newSpongeStack);
            return true;
        }
        if (!(itemMeta instanceof AbstractItemMeta)) {
            throw new IllegalArgumentException("itemMeta must be AbstractItemMeta");
        }
        //TODO check type of meta is applicable to itemtype
        this.meta = (AbstractItemMeta) itemMeta;
        return true;
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
        var currentMeta = getItemMeta();
        var itemType = SoakItemStackMap.toSponge(type).orElseThrow(() -> new RuntimeException("material is not item"));
        var itemStack = org.spongepowered.api.item.inventory.ItemStack.of(itemType, currentMeta.quantity());
        var newMeta = SoakItemStackMap.toBukkitMeta(itemStack);
        currentMeta.copyInto(newMeta);
        setItemMeta(newMeta);
    }

    @Override
    public @NotNull ItemStack withType(@NotNull Material type) {
        var copy = this.clone();
        var currentMeta = getItemMeta();
        var itemType = SoakItemStackMap.toSponge(type).orElseThrow(() -> new RuntimeException("material is not item"));
        var itemStack = org.spongepowered.api.item.inventory.ItemStack.of(itemType, currentMeta.quantity());
        var newMeta = SoakItemStackMap.toBukkitMeta(itemStack);
        currentMeta.copyInto(newMeta);
        copy.setItemMeta(newMeta);
        return copy;
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
        throw NotImplementedException.createByLazy(ItemStack.class, "getData");
    }

    @Override
    public void setData(@Nullable MaterialData data) {
        throw NotImplementedException.createByLazy(ItemStack.class, "setData", MaterialData.class);
    }

    @Override
    public short getDurability() {
        return (short) getItemMeta().durability();
    }

    @Override
    public void setDurability(short durability) {
        getItemMeta().setDurability(durability);
    }

    @Override
    public int getMaxStackSize() {
        return getItemMeta().getMaxStackSize();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemStack compareStack)) {
            return false;
        }
        if (!compareStack.hasItemMeta()) {
            return false;
        }
        var compareMeta = compareStack.getItemMeta();
        var meta = this.getItemMeta();
        return compareMeta.equals(meta);

    }

    @Override
    public boolean isSimilar(@Nullable ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (!stack.hasItemMeta()) {
            return false;
        }
        return this.getItemMeta().equalsIgnoreQuantity(stack.getItemMeta());
    }

    @Override
    public @NotNull ItemStack clone() {
        var meta = this.getItemMeta().clone();
        return new SoakItemStack(meta);
    }

    @Override
    public int hashCode() {
        return getItemMeta().sponge().hashCode();
    }

    @Override
    public boolean containsEnchantment(@NotNull Enchantment ench) {
        throw NotImplementedException.createByLazy(ItemStack.class, "containsEnchantment", Enchantment.class);
    }

    @Override
    public int getEnchantmentLevel(@NotNull Enchantment ench) {
        throw NotImplementedException.createByLazy(ItemStack.class, "getEnchantmentLevel", Enchantment.class);

    }

    @Override
    public @NotNull Map<Enchantment, Integer> getEnchantments() {
        throw NotImplementedException.createByLazy(ItemStack.class, "getEnchantments");

    }

    @Override
    public void addEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        throw NotImplementedException.createByLazy(ItemStack.class, "addEnchantments", Map.class);

    }

    @Override
    public void addEnchantment(@NotNull Enchantment ench, int level) {
        throw NotImplementedException.createByLazy(ItemStack.class, "addEnchantment", Enchantment.class, int.class);

    }

    @Override
    public void addUnsafeEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        throw NotImplementedException.createByLazy(ItemStack.class, "addUnsafeEnchantments", Map.class);

    }

    @Override
    public void addUnsafeEnchantment(@NotNull Enchantment ench, int level) {
        throw NotImplementedException.createByLazy(ItemStack.class, "addUnsafeEnchantment", Enchantment.class, int.class);

    }

    @Override
    public int removeEnchantment(@NotNull Enchantment ench) {
        throw NotImplementedException.createByLazy(ItemStack.class, "removeEnchantment", Enchantment.class);

    }

    @Override
    public void removeEnchantments() {
        throw NotImplementedException.createByLazy(ItemStack.class, "removeEnchantments");

    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        throw NotImplementedException.createByLazy(ItemStack.class, "serialize");
    }

    @Override
    public boolean editMeta(@NotNull Consumer<? super ItemMeta> consumer) {
        consumer.accept(this.getItemMeta());
        return true;
    }

    @Override
    public <M extends ItemMeta> boolean editMeta(@NotNull Class<M> metaClass, @NotNull Consumer<? super M> consumer) {
        var meta = getItemMeta();
        if (!metaClass.isInstance(meta)) {
            return false;
        }
        consumer.accept((M) meta);
        return true;
    }

    @Override
    public AbstractItemMeta getItemMeta() {
        return this.meta;
    }

    @Override
    public @NotNull String getTranslationKey() {
        var component = getItemMeta().sponge().asComponent();
        if (!(component instanceof TranslatableComponent trans)) {
            throw new IllegalStateException("ItemStack does not have translation key");
        }
        return trans.key();
    }

    @Override
    public @NotNull ItemStack enchantWithLevels(@Range(from = 1L, to = 30L) int levels, boolean allowTreasure, @NotNull Random random) {
        throw NotImplementedException.createByLazy(ItemStack.class, "enchantWithLevels", int.class, boolean.class, Random.class);
    }

    @Override
    public @NotNull ItemStack enchantWithLevels(@Range(from = 1L, to = 30L) int levels, @NotNull RegistryKeySet<@NotNull Enchantment> keySet, @NotNull Random random) {
        throw NotImplementedException.createByLazy(ItemStack.class, "enchantWithLevels", int.class, RegistryKeySet.class, Random.class);

    }

    @Override
    public @NotNull HoverEvent<HoverEvent.ShowItem> asHoverEvent(@NotNull UnaryOperator<HoverEvent.ShowItem> op) {
        throw NotImplementedException.createByLazy(ItemStack.class, "asHoverEvent", UnaryOperator.class);
    }

    @Override
    public @NotNull Component displayName() {
        return this.getItemMeta().sponge().asComponent();
    }

    @Override
    public @NotNull ItemStack ensureServerConversions() {
        throw NotImplementedException.createByLazy(ItemStack.class, "ensureServerConversions");
    }

    @Override
    public @NotNull byte[] serializeAsBytes() {
        throw NotImplementedException.createByLazy(ItemStack.class, "serializeAsBytes");
    }

    @Override
    public @Nullable String getI18NDisplayName() {
        throw NotImplementedException.createByLazy(ItemStack.class, "getI18NDisplayName");
    }

    @Override
    public int getMaxItemUseDuration() {
        return getItemMeta().maxDurability();
    }

    @Override
    public int getMaxItemUseDuration(@NotNull LivingEntity entity) {
        throw NotImplementedException.createByLazy(ItemStack.class, "getMaxItemUseDuration", LivingEntity.class);
    }

    @Override
    public @NotNull ItemStack asOne() {
        return asQuantity(1);
    }

    @Override
    public @NotNull ItemStack asQuantity(int qty) {
        var copy = clone();
        copy.setAmount(qty);
        return copy;
    }

    @Override
    public @NotNull ItemStack add() {
        return super.add(1);
    }

    @Override
    public @NotNull ItemStack add(int qty) {
        setAmount(getAmount() + qty);
        return this;
    }

    @Override
    public @NotNull ItemStack subtract() {
        return subtract(1);
    }

    @Override
    public @NotNull ItemStack subtract(int qty) {
        var newQuantity = getAmount() - qty;
        newQuantity = Math.max(newQuantity, 0);
        setAmount(newQuantity);
        return this;
    }

    @Override
    public @Nullable List<String> getLore() {
        var lore = lore();
        if (lore == null) {
            return null;
        }

        return ListMappingUtils.direct(
                lore,
                component -> LegacyComponentSerializer.legacyAmpersand().serialize(component),
                legacy -> LegacyComponentSerializer.legacySection().deserialize(legacy),
                true);
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
