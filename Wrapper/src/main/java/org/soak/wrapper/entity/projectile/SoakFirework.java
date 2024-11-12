package org.soak.wrapper.entity.projectile;

import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.wrapper.inventory.meta.SoakFireworkMeta;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.projectile.explosive.FireworkRocket;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Ticks;

import java.util.UUID;

public class SoakFirework extends AbstractProjectile<FireworkRocket> implements Firework {

    public SoakFirework(Subject subject, Audience audience, FireworkRocket entity) {
        super(subject, audience, entity);
    }

    @Override
    public @NotNull FireworkMeta getFireworkMeta() {
        //TODO double check this
        throw NotImplementedException.createByLazy(Firework.class, "getFireworkMeta");
        //return new SoakFireworkMeta(this.spongeEntity());
    }

    @Override
    public void setFireworkMeta(@NotNull FireworkMeta arg0) {
        //this is done in the item meta .... may need to fix this
    }

    @Override
    public boolean setAttachedTo(@Nullable LivingEntity livingEntity) {
        throw NotImplementedException.createByLazy(Firework.class, "setAttachedTo", LivingEntity.class);
    }

    @Override
    public @Nullable LivingEntity getAttachedTo() {
        throw NotImplementedException.createByLazy(Firework.class, "getAttachedTo");
    }

    @Override
    public boolean setLife(int i) {
        throw NotImplementedException.createByLazy(Firework.class, "setLife", int.class);
    }

    @Override
    public int getLife() {
        throw NotImplementedException.createByLazy(Firework.class, "getLife");
    }

    @Override
    public boolean setMaxLife(int i) {
        throw NotImplementedException.createByLazy(Firework.class, "setMaxLife", int.class);
    }

    @Override
    public int getMaxLife() {
        throw NotImplementedException.createByLazy(Firework.class, "getMaxLife");
    }

    @Override
    public void detonate() {
        this.spongeEntity().detonate();
    }

    @Override
    public boolean isDetonated() {
        throw NotImplementedException.createByLazy(Firework.class, "isDetonated");
    }

    @Override
    public boolean isShotAtAngle() {
        throw NotImplementedException.createByLazy(Firework.class, "isShotAtAngle");
    }

    @Override
    public void setShotAtAngle(boolean arg0) {
        throw NotImplementedException.createByLazy(Firework.class, "setShotAtAngle", boolean.class);
    }

    @Override
    public UUID getSpawningEntity() {
        return this.spongeEntity().creator().map(Value::get).orElse(null);
    }

    @Override
    public LivingEntity getBoostedEntity() {
        throw NotImplementedException.createByLazy(Firework.class, "getBoostedEntity");
    }

    @Override
    public @NotNull ItemStack getItem() {
        throw NotImplementedException.createByLazy(Firework.class, "getItem");
    }

    @Override
    public void setItem(@Nullable ItemStack itemStack) {
        throw NotImplementedException.createByLazy(Firework.class, "setItem", ItemStack.class);
    }

    @Override
    public int getTicksFlown() {
        throw NotImplementedException.createByLazy(Firework.class, "getTicksFlown");
    }

    @Override
    public void setTicksFlown(int i) {
        throw NotImplementedException.createByLazy(Firework.class, "setTicksFlown", int.class);
    }

    @Override
    public int getTicksToDetonate() {
        return this.spongeEntity().get(Keys.TICKS_REMAINING).map(Ticks::ticks).map(Long::intValue).orElse(-1);
    }

    @Override
    public void setTicksToDetonate(int i) {
        this.spongeEntity().offer(Keys.TICKS_REMAINING, Ticks.of(i));
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
