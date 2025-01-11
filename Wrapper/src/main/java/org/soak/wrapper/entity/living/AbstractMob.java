package org.soak.wrapper.entity.living;

import com.destroystokyo.paper.entity.Pathfinder;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.map.item.inventory.SoakEquipmentMap;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandPreferences;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.math.vector.Vector3d;

public abstract class AbstractMob<E extends Agent> extends AbstractLivingEntity<E> implements Mob {

    public AbstractMob(Subject subject, Audience audience, E entity) {
        super(subject, audience, entity);
    }

    @Override
    public @NotNull Pathfinder getPathfinder() {
        throw NotImplementedException.createByLazy(Mob.class, "getPathfinder");
    }

    @Override
    public boolean isInDaylight() {
        throw NotImplementedException.createByLazy(Mob.class, "isInDaylight");
    }

    @Override
    public void lookAt(@NotNull Location location) {
        lookAt(location.x(), location.y(), location.z());
    }

    @Override
    public void lookAt(@NotNull Location location, float headRotationSpeed, float maxHeadRotationPitch) {
        lookAt(location.x(), location.y(), location.z(), headRotationSpeed, maxHeadRotationPitch);
    }

    @Override
    public void lookAt(@NotNull Entity entity) {
        lookAt(entity.getLocation());
    }

    @Override
    public void lookAt(@NotNull Entity entity, float v, float v1) {
        lookAt(entity.getLocation(), v, v1);
    }

    @Override
    public void lookAt(double x, double y, double z) {
        this.spongeEntity().lookAt(new Vector3d(x, y, z));
    }

    @Override
    public void lookAt(double x, double y, double z, float headRotationSpeed, float maxHeadRotationPitch) {
        throw NotImplementedException.createByLazy(Mob.class, "lookAt", double.class, double.class, double.class, float.class, float.class);
    }

    @Override
    public int getHeadRotationSpeed() {
        throw NotImplementedException.createByLazy(Mob.class, "getHeadRotationSpeed");
    }

    @Override
    public int getMaxHeadPitch() {
        throw NotImplementedException.createByLazy(Mob.class, "getMaxHeadPitch");
    }

    @Override
    public void setTarget(@Nullable LivingEntity livingEntity) {
        throw NotImplementedException.createByLazy(Mob.class, "setTarget", LivingEntity.class);

    }

    @Override
    public @Nullable LivingEntity getTarget() {
        throw NotImplementedException.createByLazy(Mob.class, "getTarget");
    }

    @Override
    public void setAware(boolean b) {
        throw NotImplementedException.createByLazy(Mob.class, "setAware", boolean.class);
    }

    @Override
    public boolean isAware() {
        throw NotImplementedException.createByLazy(Mob.class, "isAware");

    }

    @Override
    public @Nullable Sound getAmbientSound() {
        throw NotImplementedException.createByLazy(Mob.class, "getAmbientSound");

    }

    @Override
    public boolean isAggressive() {
        throw NotImplementedException.createByLazy(Mob.class, "isAggressive");

    }

    @Override
    public void setAggressive(boolean b) {
        throw NotImplementedException.createByLazy(Mob.class, "setAggressive");
    }

    @Override
    public boolean isLeftHanded() {
        return this.spongeEntity().get(Keys.DOMINANT_HAND).map(handPref -> HandPreferences.LEFT.get().equals(handPref)).orElse(false);
    }

    @Override
    public void setLeftHanded(boolean b) {
        this.spongeEntity().offer(Keys.DOMINANT_HAND, b ? HandPreferences.LEFT.get() : HandPreferences.RIGHT.get());
    }

    @Override
    public int getPossibleExperienceReward() {
        throw NotImplementedException.createByLazy(Mob.class, "getPossibleExperienceReward");
    }

    @Override
    public boolean canUseEquipmentSlot(@NotNull EquipmentSlot equipmentSlot) {
        return this.spongeEntity().canEquip(SoakEquipmentMap.toSponge(equipmentSlot));
    }

    @Override
    public void setLootTable(@Nullable LootTable lootTable) {
        throw NotImplementedException.createByLazy(Mob.class, "setLootTable", LootTable.class);
    }

    @Override
    public @Nullable LootTable getLootTable() {
        throw NotImplementedException.createByLazy(Mob.class, "getLootTable", LootTable.class);
    }

    @Override
    public void setSeed(long l) {
        throw NotImplementedException.createByLazy(Mob.class, "setSeed", long.class);
    }

    @Override
    public long getSeed() {
        throw NotImplementedException.createByLazy(Mob.class, "getSeed");

    }
}
