package org.soak.wrapper.entity.living.animal.sheep;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;
import org.jetbrains.annotations.Nullable;
import org.soak.map.SoakColourMap;
import org.soak.wrapper.entity.living.AbstractAnimal;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.service.permission.Subject;

public class SoakSheep extends AbstractAnimal<org.spongepowered.api.entity.living.animal.Sheep> implements Sheep {

    public SoakSheep(org.spongepowered.api.entity.living.animal.Sheep sheep) {
        this(Sponge.systemSubject(), Sponge.systemSubject(), sheep);
    }

    public SoakSheep(Subject subject, Audience audience, org.spongepowered.api.entity.living.animal.Sheep entity) {
        super(subject, audience, entity);
    }

    @Override
    public void shear(Sound.Source source) {
        //TODO find out what source does
        spongeEntity().offer(Keys.IS_SHEARED, true);
    }

    @Override
    public boolean readyToBeSheared() {
        return spongeEntity().get(Keys.IS_SHEARED).map(result -> !result).orElse(false);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isSheared() {
        return !readyToBeSheared();
    }

    @Override
    public void setSheared(boolean b) {
        shear(Sound.Source.PLAYER);
    }

    @Override
    public @Nullable DyeColor getColor() {
        return this.spongeEntity().get(Keys.DYE_COLOR).map(SoakColourMap::toBukkitDye).orElse(null);
    }

    @Override
    public void setColor(DyeColor dyeColor) {
        this.spongeEntity().offer(Keys.DYE_COLOR, SoakColourMap.toSpongeDye(SoakColourMap.toSponge(dyeColor.getColor())).orElseThrow(() -> new RuntimeException("No mapping for DyeColor: " + dyeColor.name())));
    }
}
