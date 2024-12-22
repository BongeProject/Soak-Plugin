package org.soak.generate.bukkit;

import io.papermc.paper.world.flag.FeatureDependant;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Translatable;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.WrapperManager;
import org.soak.map.SoakResourceKeyMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.entity.AbstractEntity;
import org.soak.wrapper.entity.SoakEntity;
import org.soak.wrapper.entity.SoakLightningStrike;
import org.soak.wrapper.entity.living.animal.sheep.SoakSheep;
import org.soak.wrapper.entity.living.human.SoakPlayer;
import org.soak.wrapper.entity.projectile.SoakFirework;
import org.spongepowered.api.entity.EntityCategories;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.animal.Sheep;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.entity.projectile.explosive.FireworkRocket;
import org.spongepowered.api.entity.weather.LightningBolt;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Function;

public class EntityTypeList {

    public static Class<? extends Enum<?>> LOADED_CLASS;
    public static Collection<EntityTypeMappingEntry<?, ?>> ENTITIES_MAPPINGS = new LinkedTransferQueue<>();
    public static EntityTypeMappingEntry<Player, SoakPlayer> PLAYER = register(EntityTypes.PLAYER.get(), SoakPlayer.class, Player.class, player -> SoakManager.<WrapperManager>getManager().getMemoryStore().get((ServerPlayer) player));
    public static EntityTypeMappingEntry<FireworkRocket, SoakFirework> FIREWORK_ROCKET = register(EntityTypes.FIREWORK_ROCKET.get(), SoakFirework.class, FireworkRocket.class, SoakFirework::new);
    public static EntityTypeMappingEntry<LightningBolt, SoakLightningStrike> LIGHTNING = register(EntityTypes.LIGHTNING_BOLT.get(), SoakLightningStrike.class, LightningBolt.class, SoakLightningStrike::new);
    public static EntityTypeMappingEntry<Sheep, SoakSheep> SHEEP = register(EntityTypes.SHEEP.get(), SoakSheep.class, Sheep.class, SoakSheep::new);


    private static <SE extends org.spongepowered.api.entity.Entity, SSE extends AbstractEntity<? extends SE>> EntityTypeMappingEntry<SE, SSE> register(@NotNull EntityType<SE> soakEntityType, @NotNull Class<SSE> soakEntity, Class<SE> spongeEntity, @Nullable Function<SE, SSE> function) {
        var mapping = new EntityTypeMappingEntry<>(true, soakEntityType, soakEntity, spongeEntity, function);
        ENTITIES_MAPPINGS.add(mapping);
        return mapping;
    }

    public static DynamicType.Unloaded<? extends Enum<?>> createEntityTypeList() throws Exception {
        var entityTypeIterator = EntityTypes.registry().stream().iterator();
        var entityTypes = new HashSet<String>();
        entityTypes.add("UNKNOWN");
        while (entityTypeIterator.hasNext()) {
            var entityType = entityTypeIterator.next();
            var opCurrentMapping = ENTITIES_MAPPINGS.stream().filter(mappings -> mappings.spongeEntityType().equals(entityType)).findAny();
            if (opCurrentMapping.isPresent()) {
                entityTypes.add(opCurrentMapping.get().soakEntityTypeName());
                continue;
            }
            //TODO get common mappings from already registered
            var newMappings = new EntityTypeMappingEntry<>(false, entityType, SoakEntity.class, null, SoakEntity::new);
            SoakManager.getManager().getLogger().warn("No mapping for EntityType '" + entityType.key(RegistryTypes.ENTITY_TYPE).formatted() + "'. Mapping to " + newMappings.soakEntityClass().getSimpleName());

            var key = entityType.key(RegistryTypes.ENTITY_TYPE);
            var name = CommonGenerationCode.toName(key);
            entityTypes.add(name);
            ENTITIES_MAPPINGS.add(newMappings);
        }
        var classCreator = new ByteBuddy()
                .makeEnumeration(entityTypes)
                .name("org.bukkit.entity.EntityType");

        classCreator = createGetKeyMethod(classCreator);
        classCreator = createIsAliveMethod(classCreator);
        classCreator = createIsSummableMethod(classCreator);
        classCreator = createIsSpawnableMethod(classCreator);
        classCreator = createGetEntityClassMethod(classCreator);

        return classCreator.implement(FeatureDependant.class, Keyed.class, Translatable.class, net.kyori.adventure.translation.Translatable.class).make();
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> createGetKeyMethod(DynamicType.Builder<? extends Enum<?>> classCreator) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(EntityTypeList.class, classCreator, "getKey", NamespacedKey.class);
    }

    public static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> createIsAliveMethod(DynamicType.Builder<? extends Enum<?>> classCreator) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(EntityTypeList.class, classCreator, "isAlive", boolean.class);
    }

    public static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> createIsSummableMethod(DynamicType.Builder<? extends Enum<?>> classCreator) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(EntityTypeList.class, classCreator, "isSummable", boolean.class);
    }

    public static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> createIsSpawnableMethod(DynamicType.Builder<? extends Enum<?>> classCreator) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(EntityTypeList.class, classCreator, "isSpawnable", boolean.class);
    }

    public static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> createGetEntityClassMethod(DynamicType.Builder<? extends Enum<?>> classCreator) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(EntityTypeList.class, classCreator, "getEntityClass", Class.class);
    }

    public static <T extends Enum<T>> EnumSet<T> values() {
        if (LOADED_CLASS == null) {
            throw new RuntimeException("EntityTypeList.LOADED_CLASS must be set");
        }
        return EnumSet.allOf((Class<T>) LOADED_CLASS);
    }

    public static <T extends Enum<T>> T value(org.spongepowered.api.entity.EntityType<?> type) {
        String enumName = ENTITIES_MAPPINGS.stream().filter(entry -> entry.spongeEntityType().equals(type)).findAny().map(EntityTypeMappingEntry::soakEntityTypeName).orElseThrow();
        EnumSet<T> values = values();
        return values
                .stream()
                .filter(enumValue -> enumValue.name().equals(enumName))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Found EntityType name of '" + enumName + "' but couldnt find the enum"));
    }

    public static <SE extends org.spongepowered.api.entity.Entity> EntityTypeMappingEntry<SE, ?> getEntityTypeMapping(org.spongepowered.api.entity.EntityType<SE> type) {
        return ENTITIES_MAPPINGS.stream().filter(map -> map.spongeEntityType().equals(type)).findAny().map(t -> (EntityTypeMappingEntry<SE, ?>) t).orElseThrow();
    }

    public static Optional<EntityType<?>> getEntityType(Enum<?> enumEntry) {
        return getEntityTypeMapping(enumEntry).map(EntityTypeMappingEntry::spongeEntityType);
    }

    public static Optional<EntityTypeMappingEntry<?, ?>> getEntityTypeMapping(Enum<?> enumEntry) {
        return ENTITIES_MAPPINGS.stream().filter(mapping -> mapping.soakEntityTypeName().equals(enumEntry.name())).findAny();
    }

    public static NamespacedKey getKey(Enum<?> enumEntry) {
        return getEntityType(enumEntry).map(type -> type.key(RegistryTypes.ENTITY_TYPE)).map(SoakResourceKeyMap::mapToBukkit).orElseThrow(() -> new IllegalStateException(enumEntry.name() + " does not have a key"));
    }

    public static boolean isAlive(Enum<?> enumEntry) {
        return getEntityType(enumEntry).map(type -> !type.category().equals(EntityCategories.AMBIENT.get())).orElse(false);
    }

    public static boolean isSummable(Enum<?> enumEntry) {
        return getEntityType(enumEntry).map(type -> type.isSummonable()).orElse(false);
    }

    public static boolean isSpawnable(Enum<?> enumEntry) {
        return getEntityType(enumEntry).map(type -> type.canSpawnAwayFromPlayer()).orElse(false); //TODO find true method
    }

    public static Class<? extends Entity> getEntityClass(Enum<?> enumEntry) {
        return getEntityTypeMapping(enumEntry).map(EntityTypeMappingEntry::soakEntityClass).orElseThrow();
    }

}
