package org.soak.wrapper.entity;

import net.kyori.adventure.audience.Audience;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.service.permission.Subject;

public class SoakEntity<SpongeEntity extends Entity> extends AbstractEntity<SpongeEntity> {

    public SoakEntity(SpongeEntity entity) {
        this(Sponge.systemSubject(), Sponge.systemSubject(), entity);
    }

    public SoakEntity(Subject subject, Audience audience, SpongeEntity entity) {
        super(subject, audience, entity);
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
