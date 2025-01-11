package org.soak.map;

import org.bukkit.entity.Wolf;
import org.soak.generate.bukkit.EntityTypeList;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;

public class SoakEntityMap {

    public static EntityType<?> toSponge(org.bukkit.entity.EntityType type) {
        return EntityTypeList.getEntityType(type).orElseThrow(() -> new RuntimeException("Cannot find Sponge version of " + type.name()));
    }

    public static org.bukkit.entity.EntityType toBukkit(EntityType<?> type) {
        return EntityTypeList.value(type);
    }
}
