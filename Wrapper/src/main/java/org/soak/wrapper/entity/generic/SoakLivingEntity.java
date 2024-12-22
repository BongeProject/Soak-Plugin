package org.soak.wrapper.entity.generic;

import net.kyori.adventure.audience.Audience;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.soak.map.item.inventory.SoakInventoryMap;
import org.soak.wrapper.entity.living.AbstractLivingEntity;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.service.permission.Subject;

public class SoakLivingEntity<E extends Living> extends AbstractLivingEntity<E> {

    public SoakLivingEntity( E entity) {
        super(Sponge.systemSubject(), Sponge.systemSubject(), entity);
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
