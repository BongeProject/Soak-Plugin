package org.soak.generate.bukkit;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.entity.AbstractEntity;
import org.soak.wrapper.entity.SoakEntity;
import org.soak.wrapper.entity.generic.SoakLivingEntity;
import org.soak.wrapper.entity.living.AbstractLivingEntity;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.registry.RegistryTypes;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Function;

public class EntityTypeMappingEntry<SE extends Entity, SoakE extends org.bukkit.entity.Entity> {

    private final @NotNull EntityType<SE> spongeEntityType;
    private final @NotNull Class<SoakE> soakEntityClass;
    private final @Nullable Class<SE> spongeEntityClass;
    private final @NotNull String soakEntityType;
    private final boolean isFinal;

    private final @NotNull Function<SE, SoakE> creation;

    public EntityTypeMappingEntry(boolean isFinal, @NotNull EntityType<SE> soakEntityType, @NotNull Class<SoakE> soakEntity, @Nullable Class<SE> spongeEntity, @Nullable Function<SE, SoakE> function) {
        this.soakEntityClass = soakEntity;
        this.spongeEntityType = soakEntityType;
        this.spongeEntityClass = spongeEntity;
        ResourceKey key = this.spongeEntityType.key(RegistryTypes.ENTITY_TYPE);
        this.soakEntityType = CommonGenerationCode.toName(key);
        this.isFinal = isFinal;
        this.creation = function == null ? defaultCreation(this) : function;
    }

    private static <SEntity extends Entity, SoakE extends org.bukkit.entity.Entity> Function<SEntity, SoakE> defaultCreation(EntityTypeMappingEntry<SEntity, SoakE> mapping) {
        return (entity) -> {
            try {
                return mapping.soakEntityClass.getConstructor(Entity.class).newInstance(entity);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public <SoakEntity extends org.bukkit.entity.Entity> EntityTypeMappingEntry<SE, SoakEntity> updateWithSoakClass(@NotNull Class<SE> clazz) {
        if (Living.class.isAssignableFrom(clazz)) {
            EntityTypeMappingEntry<SE, AbstractLivingEntity> t = updateWithSoakClass(AbstractLivingEntity.class, e -> new SoakLivingEntity<>((Living)e), clazz);
            return (EntityTypeMappingEntry<SE, SoakEntity>) t;
        }
        return (EntityTypeMappingEntry<SE, SoakEntity>) updateWithSoakClass(this.soakEntityClass, this.creation, clazz);
    }

    public <SoakEntity extends org.bukkit.entity.Entity> EntityTypeMappingEntry<SE, SoakEntity> updateWithSoakClass(@NotNull Class<SoakEntity> soakClass, @NotNull Function<SE, SoakEntity> function, @Nullable Class<SE> spongeEntity) {
        var newMapping = new EntityTypeMappingEntry<>(true, this.spongeEntityType, soakClass, spongeEntity, function);
        SoakManager.getManager().getLogger().info("Updating mapping of " + this.spongeEntityType.key(RegistryTypes.ENTITY_TYPE).formatted() + " to " + soakClass.getSimpleName());
        EntityTypeList.ENTITIES_MAPPINGS.add(newMapping);
        EntityTypeList.ENTITIES_MAPPINGS.remove(this);
        return newMapping;
    }

    public boolean isFinal() {
        return this.isFinal;
    }

    @NotNull
    public EntityType<SE> spongeEntityType() {
        return this.spongeEntityType;
    }

    @NotNull
    public Enum<?> soakEntityType() {
        return EntityTypeList.values().stream().filter(e -> e.name().equals(this.soakEntityType)).findAny().orElseThrow();
    }

    @NotNull
    public Optional<Class<SE>> spongeEntityClass() {
        return Optional.ofNullable(this.spongeEntityClass);
    }

    @NotNull
    public Class<SoakE> soakEntityClass() {
        return this.soakEntityClass;
    }

    @NotNull
    public String soakEntityTypeName() {
        return this.soakEntityType;
    }

    @NotNull
    public SoakE createMapping(SE spongeEntity) {
        return this.creation.apply(spongeEntity);
    }
}
