package org.soak.wrapper.entity.living;

import com.destroystokyo.paper.block.TargetBlockInfo;
import com.destroystokyo.paper.entity.TargetEntityInfo;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.util.TriState;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.soak.Constants;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakBlockMap;
import org.soak.wrapper.block.SoakBlock;
import org.soak.wrapper.damage.SoakDamageSource;
import org.soak.wrapper.entity.AbstractEntity;
import org.soak.wrapper.entity.SoakAttributable;
import org.soak.wrapper.entity.SoakDamageable;
import org.soak.wrapper.entity.SoakProjectileSource;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.blockray.RayTrace;
import org.spongepowered.api.world.server.ServerWorld;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractLivingEntity<E extends Living> extends AbstractEntity<E> implements SoakDamageable, SoakAttributable, SoakProjectileSource, LivingEntity {

    public AbstractLivingEntity(Subject subject, Audience audience, E entity) {
        super(subject, audience, entity);
    }

    @Override
    public boolean canUseEquipmentSlot(@NotNull EquipmentSlot equipmentSlot) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "canUseEquipmentSlot", EquipmentSlot.class);
    }

    @Override
    public @Nullable BlockFace getTargetBlockFace(int i, @NotNull FluidCollisionMode fluidCollisionMode) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getTargetBlockFace", FluidCollisionMode.class);
    }

    @Override
    public @Nullable RayTraceResult rayTraceEntities(int i, boolean b) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "rayTraceEntities", int.class, boolean.class);
    }

    @Override
    public void setArrowsInBody(int i, boolean b) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setArrowsInBody", int.class, boolean.class);
    }

    @Override
    public int getNextArrowRemoval() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getNextArrowRemoval");
    }

    @Override
    public void setNextArrowRemoval(@Range(from = 0L, to = 2147483647L) int i) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setNextArrowRemoval", int.class);
    }

    @Override
    public int getBeeStingerCooldown() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getBeeStingerCooldown");
    }

    @Override
    public void setBeeStingerCooldown(int i) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setBeeStingerCooldown", int.class);
    }

    @Override
    public int getBeeStingersInBody() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getBeeStingersInBody");
    }

    @Override
    public void setBeeStingersInBody(int i) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setBeeStingersInBody", int.class);
    }

    @Override
    public int getNextBeeStingerRemoval() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getNextBeeStingerRemoval");
    }

    @Override
    public void setNextBeeStingerRemoval(@Range(from = 0L, to = 2147483647L) int i) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setNextBeeStingerRemoval", int.class);
    }

    @Override
    public boolean isClimbing() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "isClimbing");
    }

    @Override
    public @Nullable Sound getHurtSound() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getHurtSound");
    }

    @Override
    public @Nullable Sound getDeathSound() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getDeathSound");
    }

    @Override
    public @NotNull Sound getFallDamageSound(int i) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getFallDamageSound");
    }

    @Override
    public @NotNull Sound getFallDamageSoundSmall() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getFallDamageSoundSmall");

    }

    @Override
    public @NotNull Sound getFallDamageSoundBig() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getFallDamageSoundBig");
    }

    @Override
    public @NotNull Sound getDrinkingSound(@NotNull ItemStack itemStack) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getDrinkingSound", ItemStack.class);
    }

    @Override
    public @NotNull Sound getEatingSound(@NotNull ItemStack itemStack) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getEatingSound", ItemStack.class);
    }

    @Override
    public boolean canBreatheUnderwater() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "canBreatheUnderwater");
    }

    @Override
    public void knockback(double v, double v1, double v2) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "knockback", double.class, double.class, double.class);
    }

    @Override
    public void broadcastSlotBreak(@NotNull EquipmentSlot equipmentSlot) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "broadcastSlotBreak", EquipmentSlot.class);
    }

    @Override
    public void broadcastSlotBreak(@NotNull EquipmentSlot equipmentSlot, @NotNull Collection<Player> collection) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "broadcastSlotBreak", EquipmentSlot.class, Collection.class);
    }

    @Override
    public @NotNull ItemStack damageItemStack(@NotNull ItemStack itemStack, int i) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "damageItemStack", ItemStack.class, int.class);
    }

    @Override
    public void damageItemStack(@NotNull EquipmentSlot equipmentSlot, int i) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "damageItemStack", EquipmentSlot.class, int.class);
    }

    @Override
    public float getBodyYaw() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getBodyYaw");
    }

    @Override
    public void setBodyYaw(float v) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setBodyYaw", float.class);
    }

    @Override
    public @NotNull TriState getFrictionState() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getFrictionState");
    }

    @Override
    public void setFrictionState(@NotNull TriState triState) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setFrictionState", TriState.class);
    }

    @Override
    public RayTraceResult rayTraceBlocks(double arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "rayTraceBlocks", double.class);
    }

    @Override
    public RayTraceResult rayTraceBlocks(double arg0, @NotNull FluidCollisionMode arg1) {
        throw NotImplementedException.createByLazy(LivingEntity.class,
                "rayTraceBlocks",
                double.class,
                FluidCollisionMode.class);
    }

    @Override
    @Deprecated
    public TargetEntityInfo getTargetEntityInfo(int arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getTargetEntityInfo", int.class, boolean.class);
    }

    @Override
    public @NotNull List<Block> getLastTwoTargetBlocks(@Nullable Set<Material> transparent, int maxDistance) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getLastTwoTargetBlocks", Set.class, int.class);
    }

    @Override
    public void playPickupItemAnimation(@NotNull Item arg0, int arg1) {
        throw NotImplementedException.createByLazy(LivingEntity.class,
                "playPickupItemAnimation",
                Item.class,
                int.class);
    }

    @Override
    public Entity getTargetEntity(int arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getTargetEntity", int.class, boolean.class);
    }

    @Override
    public double getEyeHeight() {
        return this.spongeEntity().eyeHeight().get();
    }

    @Override
    public double getEyeHeight(boolean ignorePose) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getEyeHeight", boolean.class);
    }

    @Override
    public @NotNull Location getEyeLocation() {
        var position = this.spongeEntity().eyePosition().get();
        return new Location(getWorld(), position.x(), position.y(), position.z());
    }

    @Override
    public @NotNull List<Block> getLineOfSight(@Nullable Set<Material> transparent, int maxDistance) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getLineOfSight", Set.class, int.class);
    }

    @Override
    public @NotNull Block getTargetBlock(@Nullable Set<Material> transparent, int maxDistance) {
        var opBlock = RayTrace.block()
                .direction(this.spongeEntity())
                .sourceEyePosition(this.spongeEntity())
                .continueWhileBlock((locatableBlock -> {
                    if (transparent == null) {
                        return false;
                    }
                    return transparent.stream()
                            .map(mat -> SoakBlockMap.toSponge(mat)
                                    .orElseThrow(() -> new RuntimeException(mat.name() + " is not a block")))
                            .anyMatch(type -> type.equals(locatableBlock.blockState().type()));
                }))
                .limit(maxDistance)
                .execute();
        return opBlock.flatMap(block -> block.selectedObject().location().onServer())
                .map(SoakBlock::new)
                .orElseGet(() -> new SoakBlock((ServerWorld) this.spongeEntity().world(),
                        this.spongeEntity().eyePosition().get().toInt()));
    }

    @Override
    @Deprecated
    public @Nullable Block getTargetBlock(int maxDistance, TargetBlockInfo.FluidMode fluidMode) {
        throw NotImplementedException.createByLazy(LivingEntity.class,
                "getTargetBlockExact",
                int.class,
                FluidCollisionMode.class);
    }

    @Override
    @Deprecated
    public @Nullable BlockFace getTargetBlockFace(int maxDistance, @NotNull TargetBlockInfo.FluidMode fluidMode) {
        throw NotImplementedException.createByLazy(LivingEntity.class,
                "getTargetBlockFace",
                int.class,
                TargetBlockInfo.FluidMode.class);
    }

    @Override
    @Deprecated
    public @Nullable TargetBlockInfo getTargetBlockInfo(int maxDistance, @NotNull TargetBlockInfo.FluidMode fluidMode) {
        throw NotImplementedException.createByLazy(LivingEntity.class,
                "getTargetBlockInfo",
                int.class,
                TargetBlockInfo.FluidMode.class);
    }

    @Override
    public Block getTargetBlockExact(int arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getTargetBlockExact", int.class);
    }

    @Override
    public @Nullable Block getTargetBlockExact(int maxDistance, @NotNull FluidCollisionMode fluidCollisionMode) {
        throw NotImplementedException.createByLazy(LivingEntity.class,
                "getTargetBlockExact",
                int.class,
                FluidCollisionMode.class);
    }

    @Override
    public int getRemainingAir() {
        return this.spongeEntity().getInt(Keys.REMAINING_AIR).orElse(Constants.NOT_APPLICABLE_INT);
    }

    @Override
    public void setRemainingAir(int arg0) {
        this.spongeEntity().offer(Keys.REMAINING_AIR, arg0);
    }

    @Override
    public int getMaximumAir() {
        return this.spongeEntity().getInt(Keys.MAX_AIR).orElse(Constants.NOT_APPLICABLE_INT);
    }

    @Override
    public void setMaximumAir(int arg0) {
        this.spongeEntity().offer(Keys.MAX_AIR, arg0);
    }

    @Override
    public @Nullable ItemStack getItemInUse() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getItemInUse");
    }

    @Override
    public int getItemInUseTicks() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getItemInUseTicks");
    }

    @Override
    public void setItemInUseTicks(int i) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setItemInUseTicks", int.class);
    }

    @Override
    public int getArrowCooldown() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getArrowCooldown");
    }

    @Override
    public void setArrowCooldown(int arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setArrowCooldown", int.class);
    }

    @Override
    public int getArrowsInBody() {
        return this.spongeEntity().get(Keys.STUCK_ARROWS).orElse(Constants.NOT_APPLICABLE_INT);
    }

    @Override
    public void setArrowsInBody(int arg0) {
        this.spongeEntity().offer(Keys.STUCK_ARROWS, arg0);
    }

    @Override
    public int getMaximumNoDamageTicks() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getMaximumNoDamageTicks");
    }

    @Override
    public void setMaximumNoDamageTicks(int arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setMaximumNoDamageTicks", int.class);
    }

    @Override
    public double getLastDamage() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getLastDamage");
    }

    @Override
    public void setLastDamage(double arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setLastDamage", double.class);
    }

    @Override
    public int getNoDamageTicks() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getNoDamageTicks");
    }

    @Override
    public void setNoDamageTicks(int arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setNoDamageTicks", int.class);
    }

    @Override
    public int getNoActionTicks() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getNoActionsTicks");
    }

    @Override
    public void setNoActionTicks(int i) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setNoActionTicks", int.class);
    }

    @Override
    public Player getKiller() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getKiller");
    }

    @Override
    public void setKiller(Player arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setKiller", Player.class);
    }

    @Deprecated
    @Override
    public boolean addPotionEffect(@NotNull PotionEffect arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(LivingEntity.class,
                "addPotionEffect",
                PotionEffect.class,
                boolean.class);
    }

    @Override
    public boolean addPotionEffect(@NotNull PotionEffect arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "addPotionEffect", PotionEffect.class);
    }

    @Override
    public boolean addPotionEffects(@NotNull Collection arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "addPotionEffects", Collection.class);
    }

    @Override
    public boolean hasPotionEffect(@NotNull PotionEffectType arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "hasPotionEffect", PotionEffectType.class);
    }

    @Override
    public PotionEffect getPotionEffect(@NotNull PotionEffectType arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getPotionEffect", PotionEffectType.class);
    }

    @Override
    public void removePotionEffect(@NotNull PotionEffectType arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "removePotionEffect", PotionEffectType.class);
    }

    @Override
    public @NotNull Collection<PotionEffect> getActivePotionEffects() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getActivePotionEffects");
    }

    @Override
    public boolean clearActivePotionEffects() {
        return this.spongeEntity().offer(Keys.POTION_EFFECTS, Collections.emptyList()).isSuccessful();
    }

    @Override
    public boolean hasLineOfSight(@NotNull Location arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "hasLineOfSight", Location.class);
    }

    @Override
    public boolean hasLineOfSight(@NotNull Entity arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "hasLineOfSight", Entity.class);
    }

    @Override
    protected boolean isIn(Supplier<BlockType> blockType) {
        var eyePos = this.spongeEntity().eyePosition().get();
        var eyeIn = this.spongeEntity().world().location(eyePos).blockType().equals(blockType.get());
        if (eyeIn) {
            return true;
        }
        return this.spongeEntity().location().blockType().equals(blockType.get());
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getRemoveWhenFarAway");
    }

    @Override
    public void setRemoveWhenFarAway(boolean arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setRemoveWhenFarAway", boolean.class);
    }

    @Override
    public EntityEquipment getEquipment() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getEquipment");
    }

    @Override
    public boolean getCanPickupItems() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getCanPickupItems");
    }

    @Override
    public void setCanPickupItems(boolean arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setCanPickupItems", boolean.class);
    }

    @Override
    public boolean isLeashed() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "isLeashed");
    }

    @Override
    public @NotNull Entity getLeashHolder() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getLeashHolder");
    }

    @Override
    public boolean setLeashHolder(Entity arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setLeashHolder", Entity.class);
    }

    @Override
    public boolean isGliding() {
        return this.spongeEntity().get(Keys.IS_ELYTRA_FLYING).orElse(false);
    }

    @Override
    public void setGliding(boolean arg0) {
        this.spongeEntity().offer(Keys.IS_ELYTRA_FLYING, arg0);
    }

    @Override
    public boolean isSwimming() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "isSwimming");
    }

    @Override
    public void setSwimming(boolean arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setSwimming", boolean.class);
    }

    @Override
    public boolean isRiptiding() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "isRiptiding");
    }

    @Override
    public void setRiptiding(boolean b) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setRiptiding", boolean.class);
    }

    @Override
    public boolean isSleeping() {
        return this.spongeEntity().get(Keys.IS_SLEEPING).orElse(false);
    }

    @Override
    public void setAI(boolean arg0) {
        this.spongeEntity().offer(Keys.IS_AI_ENABLED, arg0);
    }

    @Override
    public boolean hasAI() {
        return this.spongeEntity().get(Keys.IS_AI_ENABLED).orElse(false);
    }

    @Override
    public void attack(@NotNull Entity arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "attack", Entity.class);
    }

    @Override
    public void swingMainHand() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "swingMainHand");
    }

    @Override
    public void swingOffHand() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "swingOffHand");
    }

    @Override
    public void playHurtAnimation(float v) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "playHurtAnimation", float.class);
    }

    @Override
    public boolean isCollidable() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "isCollidable");
    }

    @Override
    public void setCollidable(boolean arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setCollidable", boolean.class);
    }

    @Override
    public @NotNull Set<UUID> getCollidableExemptions() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getCollidableExemptions");
    }

    @Override
    public <T> @Nullable T getMemory(@NotNull MemoryKey<T> memoryKey) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getMemory", MemoryKey.class);
    }

    @Override
    @Deprecated
    public @NotNull EntityCategory getCategory() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getCategory");
    }

    @Override
    public boolean isInvisible() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "isInvisible");
    }

    @Override
    public void setInvisible(boolean arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setInvisible", boolean.class);
    }

    //wait there is a difference?
    @Override
    public int getArrowsStuck() {
        return getArrowsInBody();
    }

    @Override
    public void setArrowsStuck(int arg0) {
        this.setArrowsInBody(arg0);
    }

    @Override
    public int getShieldBlockingDelay() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getShieldBlockingDelay");
    }

    @Override
    public void setShieldBlockingDelay(int arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setShieldBlockingDelay", int.class);
    }

    @Override
    public float getSidewaysMovement() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getSidewaysMovement");
    }

    @Override
    public float getUpwardsMovement() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getUpwardsMovement");
    }

    @Override
    public float getForwardsMovement() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getForwardsMovement");
    }

    @Override
    public void startUsingItem(@NotNull EquipmentSlot equipmentSlot) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "startUsingItem", EquipmentSlot.class);
    }

    @Override
    public void completeUsingActiveItem() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "completeUsingActiveItem");
    }

    @Override
    public ItemStack getActiveItem() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getActiveItem");
    }

    @Override
    public void clearActiveItem() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "clearActiveItem");
    }

    @Override
    public int getActiveItemRemainingTime() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getActiveItemRemainingTime");
    }

    @Override
    public void setActiveItemRemainingTime(@Range(from = 0L, to = 2147483647L) int i) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setActiveItemRemainingTime", int.class);
    }

    @Override
    public boolean hasActiveItem() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "hasActiveItem");
    }

    @Override
    public int getActiveItemUsedTime() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getActiveItemUsedTime");
    }

    @Override
    public @NotNull EquipmentSlot getActiveItemHand() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getActiveItemHand");
    }

    @Override
    public int getItemUseRemainingTime() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getItemUseRemainingTime");
    }

    @Override
    public int getHandRaisedTime() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getHandRaisedTime");
    }

    @Override
    public boolean isHandRaised() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "isHandRaised");
    }

    @Override
    public @NotNull EquipmentSlot getHandRaised() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getHandRaised");
    }

    @Override
    public boolean isJumping() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "isJumping");
    }

    @Override
    public void setJumping(boolean arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setJumping", boolean.class);
    }

    @Override
    public float getHurtDirection() {
        throw NotImplementedException.createByLazy(LivingEntity.class, "getHurtDirection");
    }

    @Override
    public void setHurtDirection(float arg0) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setHurtDirection", float.class);
    }

    @Override
    public void setMemory(@NotNull MemoryKey arg0, Object arg1) {
        throw NotImplementedException.createByLazy(LivingEntity.class, "setMemory", MemoryKey.class, Object.class);
    }


    @Override
    public void damage(double v, @NotNull DamageSource damageSource) {
        this.spongeEntity().damage(v, ((SoakDamageSource) damageSource).spongeSource());
    }

    @Override
    public void heal(double v, EntityRegainHealthEvent.RegainReason regainReason) {
        //TODO regain
        this.heal(v);
    }

    @Override
    public <T extends Projectile> @NotNull T launchProjectile(@NotNull Class<? extends T> aClass, @Nullable Vector vector, @Nullable Consumer<? super T> consumer) {
        if (consumer == null && vector == null) {
            return launchProjectile(aClass);
        }
        var projectile = launchProjectile(aClass, vector);
        if (consumer == null) {
            consumer.accept(projectile);
        }
        return projectile;
    }
}
