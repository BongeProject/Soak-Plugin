package org.soak.wrapper.entity.living.human;

import net.kyori.adventure.audience.Audience;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.wrapper.entity.living.AbstractLivingEntity;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.Humanoid;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.permission.Subject;

import java.util.Collection;
import java.util.Set;

public abstract class AbstractHumanBase<E extends Humanoid> extends AbstractLivingEntity<E> implements HumanEntity {

    public AbstractHumanBase(Subject subject, Audience audience, E entity) {
        super(subject, audience, entity);
    }

    @Override
    public void startRiptideAttack(int i, float v, @Nullable ItemStack itemStack) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "startRiptideAttack", int.class, float.class, ItemStack.class);
    }

    @Override
    public boolean canUseEquipmentSlot(@NotNull EquipmentSlot equipmentSlot) {
        var equipmentType = EquipmentTypes
                .registry()
                .stream()
                .filter(type -> equipmentSlot.name().equalsIgnoreCase(type.key(RegistryTypes.EQUIPMENT_TYPE).value()))
                .findAny()
                .orElseThrow();
        //TODO move to map
        return this.entity.canEquip(equipmentType);
    }

    @Override
    public int getEnchantmentSeed() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getEnchantmentSeed");
    }

    @Override
    public void setEnchantmentSeed(int i) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "setEnchantmentSeed", int.class);
    }

    @Override
    public @Nullable FishHook getFishHook() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getFishHook");
    }

    @Override
    public @Nullable ItemStack getItemInUse() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getItemInUse");
    }

    @Override
    public @Nullable Location getLastDeathLocation() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getLastDeathLocation");
    }

    @Override
    public void setLastDeathLocation(@Nullable Location location) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "setLastDeathLocation", Location.class);
    }

    @Override
    public @Nullable Firework fireworkBoost(@NotNull ItemStack itemStack) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "fireworkBoost", ItemStack.class);
    }

    @Deprecated
    @Override
    public Entity getShoulderEntityLeft() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getShoulderEntityLeft");
    }

    @Deprecated
    @Override
    public void setShoulderEntityLeft(Entity arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "setShoulderEntityLeft", Entity.class);
    }

    @Override
    public @NotNull Inventory getEnderChest() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getEnderChest");
    }

    @Override
    public @NotNull MainHand getMainHand() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getMainHand");
    }

    @Override
    public boolean setWindowProperty(InventoryView.@NotNull Property arg0, int arg1) {
        throw NotImplementedException.createByLazy(HumanEntity.class,
                "setWindowProperty",
                InventoryView.Property.class,
                int.class);
    }

    @Override
    public @NotNull InventoryView getOpenInventory() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getOpenInventory");
    }

    @Override
    public InventoryView openWorkbench(Location arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "openWorkbench", Location.class, boolean.class);
    }

    @Override
    public InventoryView openEnchanting(Location arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "openEnchanting", Location.class, boolean.class);
    }

    @Override
    public InventoryView openMerchant(@NotNull Merchant arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "openMerchant", Merchant.class, boolean.class);
    }

    @Override
    public InventoryView openMerchant(@NotNull Villager arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "openMerchant", Villager.class, boolean.class);
    }

    @Override
    public InventoryView openAnvil(Location arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "openAnvil", Location.class, boolean.class);
    }

    @Override
    public InventoryView openCartographyTable(Location arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(HumanEntity.class,
                "openCartographyTable",
                Location.class,
                boolean.class);
    }

    @Override
    public InventoryView openGrindstone(Location arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "openGrindstone", Location.class, boolean.class);
    }

    @Override
    public InventoryView openLoom(Location arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "openLoom", Location.class, boolean.class);
    }

    @Override
    public InventoryView openSmithingTable(Location arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(HumanEntity.class,
                "openSmithingTable",
                Location.class,
                boolean.class);
    }

    @Override
    public InventoryView openStonecutter(Location arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "openStonecutter", Location.class, boolean.class);
    }

    @Deprecated
    @Override
    public @NotNull ItemStack getItemInHand() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getItemInHand");
    }

    @Deprecated
    @Override
    public void setItemInHand(ItemStack arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "setItemInHand", ItemStack.class);
    }

    @Override
    public @NotNull ItemStack getItemOnCursor() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getItemOnCursor");
    }

    @Override
    public void setItemOnCursor(ItemStack arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "setItemOnCursor", ItemStack.class);
    }

    @Override
    public boolean hasCooldown(@NotNull Material arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "hasCooldown", Material.class);
    }

    @Override
    public int getCooldown(@NotNull Material arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getCooldown", Material.class);
    }

    @Override
    public void setCooldown(@NotNull Material arg0, int arg1) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "setCooldown", Material.class, int.class);
    }

    @Override
    public boolean isDeeplySleeping() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "isDeeplySleeping");
    }

    @Override
    public int getSleepTicks() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getSleepTicks");
    }

    @Override
    public Location getPotentialBedLocation() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getPotentialBedLocation");
    }

    @Override
    public void wakeup(boolean arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "wakeup", boolean.class);
    }

    @Override
    public @NotNull Location getBedLocation() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getBedLocation");
    }

    @Override
    public @NotNull GameMode getGameMode() {
        var spongeMode = this.spongeEntity()
                .get(Keys.GAME_MODE)
                .orElseThrow(() -> new RuntimeException("Cannot get Gamemode from " + spongeEntity().getClass()
                        .getName()));
        return GameMode.sponge(spongeMode);
    }

    @Override
    public void setGameMode(@NotNull GameMode arg0) {
        this.spongeEntity().offer(Keys.GAME_MODE, arg0.sponge());
    }

    @Override
    public boolean isHandRaised() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "isHandRaised");
    }

    @Override
    public int getExpToLevel() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getExpToLevel");
    }

    @Override
    public Entity releaseLeftShoulderEntity() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "releaseLeftShoulderEntity");
    }

    @Override
    public Entity releaseRightShoulderEntity() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "releaseRightShoulderEntity");
    }

    @Override
    public float getAttackCooldown() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getAttackCooldown");
    }

    @Override
    public boolean discoverRecipe(@NotNull NamespacedKey arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "discoverRecipe", NamespacedKey.class);
    }

    @Override
    public int discoverRecipes(@NotNull Collection<NamespacedKey> recipes) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "discoverRecipes", Collection.class);
    }

    @Override
    public boolean undiscoverRecipe(@NotNull NamespacedKey arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "undiscoverRecipe", NamespacedKey.class);
    }

    @Override
    public int undiscoverRecipes(@NotNull Collection<NamespacedKey> recipes) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "undiscoverRecipes", Collection.class);
    }

    @Override
    public boolean hasDiscoveredRecipe(@NotNull NamespacedKey arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "hasDiscoveredRecipe", NamespacedKey.class);
    }

    @Override
    public @NotNull Set<NamespacedKey> getDiscoveredRecipes() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getDiscoveredRecipes");
    }

    @Deprecated
    @Override
    public Entity getShoulderEntityRight() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getShoulderEntityRight");
    }

    @Deprecated
    @Override
    public void setShoulderEntityRight(Entity arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "setShoulderEntityRight", Entity.class);
    }

    @Override
    public void openSign(Sign arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "openSign", Sign.class);
    }

    @Override
    public void openSign(@NotNull Sign sign, @NotNull Side side) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "openSign", Sign.class, Side.class);
    }

    @Override
    public boolean dropItem(boolean arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "dropItem", boolean.class);
    }

    @Override
    public float getExhaustion() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getExhaustion");
    }

    @Override
    public void setExhaustion(float arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "setExhaustion", float.class);
    }

    @Override
    public float getSaturation() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getSaturation");
    }

    @Override
    public void setSaturation(float arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "setSaturation", float.class);
    }

    @Override
    public int getFoodLevel() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getFoodLevel");
    }

    @Override
    public void setFoodLevel(int arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "setFoodLevel", int.class);
    }

    @Override
    public int getSaturatedRegenRate() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getSaturatedRegenRate");
    }

    @Override
    public void setSaturatedRegenRate(int arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "setSaturatedRegenRate", int.class);
    }

    @Override
    public int getUnsaturatedRegenRate() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getUnsaturatedRegenRate");
    }

    @Override
    public void setUnsaturatedRegenRate(int arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "setUnsaturatedRegenRate", int.class);
    }

    @Override
    public int getStarvationRate() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getStarvationRate");
    }

    @Override
    public void setStarvationRate(int arg0) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "setStarvationRate", int.class);
    }

    @Override
    public boolean isBlocking() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "isBlocking");
    }

    @Override
    public String getName() {
        throw NotImplementedException.createByLazy(HumanEntity.class, "getName");
    }

    @Override
    public boolean sleep(Location arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(HumanEntity.class, "sleep", Location.class, boolean.class);
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
