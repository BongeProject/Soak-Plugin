package org.soak.wrapper.damage;

import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakLocationMap;
import org.soak.wrapper.entity.SoakEntity;
import org.spongepowered.api.world.server.ServerWorld;

public class SoakDamageSource implements DamageSource {

    private final org.spongepowered.api.event.cause.entity.damage.source.DamageSource source;
    private final ServerWorld world;

    public SoakDamageSource(org.spongepowered.api.event.cause.entity.damage.source.DamageSource source, ServerWorld world) {
        this.source = source;
        this.world = world;
    }

    public org.spongepowered.api.event.cause.entity.damage.source.DamageSource spongeSource() {
        return this.source;
    }

    @Override
    public @NotNull DamageType getDamageType() {
        throw NotImplementedException.createByLazy(DamageSource.class, "getDamageType");
    }

    @Override
    public @Nullable Entity getCausingEntity() {
        return this.source.indirectSource().map(SoakEntity::wrapEntity).orElse(null);
    }

    @Override
    public @Nullable Entity getDirectEntity() {
        return this.source.source().map(SoakEntity::wrapEntity).orElse(null);
    }

    @Override
    public @Nullable Location getDamageLocation() {
        return this.source.position().map(this.world::location).map(SoakLocationMap::toBukkit).orElse(null);
    }

    @Override
    public @Nullable Location getSourceLocation() {
        return this.source.location().map(SoakLocationMap::toBukkit).orElse(null);
    }

    @Override
    public boolean isIndirect() {
        return this.source.indirectSource().isPresent();
    }

    @Override
    public float getFoodExhaustion() {
        throw NotImplementedException.createByLazy(DamageSource.class, "getFoodExhaustion");
    }

    @Override
    public boolean scalesWithDifficulty() {
        return this.source.isScaledByDifficulty();
    }
}
