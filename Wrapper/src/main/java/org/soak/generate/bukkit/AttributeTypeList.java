package org.soak.generate.bukkit;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import org.bukkit.Keyed;
import org.bukkit.Translatable;
import org.spongepowered.api.entity.attribute.type.AttributeType;
import org.spongepowered.api.entity.attribute.type.AttributeTypes;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.EnumSet;
import java.util.HashSet;

public class AttributeTypeList {

    public static Class<? extends Enum<?>> LOADED_CLASS;

    public static DynamicType.Unloaded<? extends Enum<?>> createEntityTypeList() throws Exception {
        var attributeIterator = AttributeTypes.registry().stream().iterator();
        var attributes = new HashSet<String>();
        while (attributeIterator.hasNext()) {
            var attribute = attributeIterator.next();
            var key = attribute.key(RegistryTypes.ATTRIBUTE_TYPE);
            var name = CommonGenerationCode.toName(key);
            attributes.add(name);
        }
        var classCreator = new ByteBuddy()
                .makeEnumeration(attributes)
                .name("org.bukkit.Attribute");

        return classCreator.implement(Keyed.class, Translatable.class, net.kyori.adventure.translation.Translatable.class).make();
    }

    public static <T extends Enum<T>> EnumSet<T> values() {
        if (LOADED_CLASS == null) {
            throw new RuntimeException("EntityTypeList.LOADED_CLASS must be set");
        }
        return EnumSet.allOf((Class<T>) LOADED_CLASS);
    }

    public static <T extends Enum<T>> T value(AttributeType type) {
        var enumName = CommonGenerationCode.toName(type.key(RegistryTypes.ATTRIBUTE_TYPE));
        EnumSet<T> values = values();
        return values
                .stream()
                .filter(v -> v.name().equals(enumName))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Found AttributeType name of '" + enumName + "' but couldnt find the enum"));
    }

    public static AttributeType getAttributeType(Enum<?> enumEntry) {
        return AttributeTypes.registry().stream().filter(t -> CommonGenerationCode.toName(t.key(RegistryTypes.ATTRIBUTE_TYPE)).equals(enumEntry.name())).findAny().orElseThrow(() -> new IllegalArgumentException("Could not find a registered AttributeType with the enum mapped name of '" + enumEntry.name() + "'"));
    }
}
