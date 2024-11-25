package org.soak.wrapper.entity.living;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.service.permission.Subject;

import java.util.UUID;

public abstract class AbstractAnimal<E extends Animal> extends AbstractMob<E> implements Animals {

    public AbstractAnimal(Subject subject, Audience audience, E entity) {
        super(subject, audience, entity);
    }

    @Override
    public @Nullable UUID getBreedCause() {
        return this.spongeEntity().get(Keys.BREEDER).orElse(null);
    }

    @Override
    public void setBreedCause(@Nullable UUID uuid) {
        this.spongeEntity().offer(Keys.BREEDER, uuid);
    }

    @Override
    public boolean isLoveMode() {
        throw NotImplementedException.createByLazy(AbstractAnimal.class, "isLoveMode");
    }

    @Override
    public int getLoveModeTicks() {
        throw NotImplementedException.createByLazy(AbstractAnimal.class, "getLoveModeTicks");
    }

    @Override
    public void setLoveModeTicks(int i) {
        throw NotImplementedException.createByLazy(AbstractAnimal.class, "setLoveModeTicks", int.class);
    }

    @Override
    public boolean isBreedItem(@NotNull ItemStack itemStack) {
        throw NotImplementedException.createByLazy(Animals.class, "isBreedItem", ItemStack.class);
    }

    @Override
    public boolean isBreedItem(@NotNull Material material) {
        throw NotImplementedException.createByLazy(Animals.class, "isBreedItem", Material.class);
    }

    @Override
    public int getAge() {
        return this.spongeEntity().get(Keys.AGE).orElse(0);
    }

    @Override
    public void setAge(int i) {
        this.spongeEntity().offer(Keys.AGE, i);
    }

    @Override
    public void setAgeLock(boolean b) {
        throw NotImplementedException.createByLazy(Animals.class, "setAgeLock", boolean.class);
    }

    @Override
    public boolean getAgeLock() {
        throw NotImplementedException.createByLazy(Animals.class, "getAgeLock");
    }

    @Override
    public void setBaby() {
        setAdult(false);
    }

    @Override
    public void setAdult() {
        setAdult(true);
    }

    private void setAdult(boolean adult) {
        this.spongeEntity().offer(Keys.IS_ADULT, adult);
    }

    @Override
    public boolean isAdult() {
        return this.spongeEntity().get(Keys.IS_ADULT).orElse(true);
    }

    @Override
    public boolean canBreed() {
        return this.spongeEntity().get(Keys.CAN_BREED).orElse(false);
    }

    @Override
    public void setBreed(boolean b) {
        this.spongeEntity().offer(Keys.CAN_BREED, true);
    }
}
