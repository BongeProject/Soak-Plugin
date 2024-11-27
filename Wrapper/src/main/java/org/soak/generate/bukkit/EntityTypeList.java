package org.soak.generate.bukkit;

import io.papermc.paper.world.flag.FeatureDependant;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Translatable;
import org.soak.map.SoakResourceKeyMap;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.*;

public class EntityTypeList {

    public static final Map<String, org.spongepowered.api.entity.EntityType<?>> ENTITY_TYPE_MAP = new HashMap<>();
    public static Class<?> LOADED_CLASS;

    public static DynamicType.Unloaded<? extends Enum<?>> createEntityTypeList() throws Exception {
        var entityTypeIterator = EntityTypes.registry().stream().iterator();
        var entityTypes = new HashSet<String>();
        entityTypes.add("UNKNOWN");
        while (entityTypeIterator.hasNext()) {
            var entityType = entityTypeIterator.next();
            var key = entityType.key(RegistryTypes.ENTITY_TYPE);
            var name = CommonGenerationCode.toName(key);
            entityTypes.add(name);
            ENTITY_TYPE_MAP.put(name, entityType);
        }
        var classCreator = new ByteBuddy()
                .makeEnumeration(entityTypes)
                .name("org.bukkit.entity.EntityType");

        classCreator = createGetKeyMethod(classCreator);

        return classCreator.implement(FeatureDependant.class, Keyed.class, Translatable.class, net.kyori.adventure.translation.Translatable.class).make();
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<? extends Enum<?>> createGetKeyMethod(DynamicType.Builder<? extends Enum<?>> classCreator) throws NoSuchMethodException {
        return CommonGenerationCode.callMethod(EntityTypeList.class, classCreator, "getKey", NamespacedKey.class);
    }

    public static <T extends Enum<T>> EnumSet<T> values() {
        if (LOADED_CLASS == null) {
            throw new RuntimeException("EntityTypeList.LOADED_CLASS must be set");
        }
        return EnumSet.allOf((Class<T>) LOADED_CLASS);
    }

    public static <T extends Enum<T>> T value(org.spongepowered.api.entity.EntityType<?> type) {
        var enumName = ENTITY_TYPE_MAP
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(type))
                .findAny()
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException(type.key(RegistryTypes.ENTITY_TYPE).asString() + " is not in EntityType enum"));
        EnumSet<T> values = values();
        return values
                .stream()
                .filter(enumValue -> enumValue.name().equals(enumName))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Found EntityType name of '" + enumName + "' but couldnt find the enum"));
    }

    public static Optional<EntityType<?>> getEntityType(Enum<?> enumEntry) {
        return Optional.ofNullable(ENTITY_TYPE_MAP.get(enumEntry.name()));
    }

    public static NamespacedKey getKey(Enum<?> enumEntry) {
        return getEntityType(enumEntry).map(type -> type.key(RegistryTypes.ENTITY_TYPE)).map(SoakResourceKeyMap::mapToBukkit).orElseThrow(() -> new IllegalStateException(enumEntry.name() + " does not have a key"));
    }

}
