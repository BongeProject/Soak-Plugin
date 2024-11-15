package org.soak.map;

import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;

public class SoakEntityMap {

    public static EntityType<?> toSponge(org.bukkit.entity.EntityType type) {
        System.err.println("EntityType class needs to be converted to ByteBuddy");
        return EntityTypes.CREEPER.get();
    }

    public static org.bukkit.entity.EntityType toBukkit(EntityType<?> type) {
        System.err.println("EntityType class needs to be converted to ByteBuddy");
        return org.bukkit.entity.EntityType.CREEPER;
    }
}
