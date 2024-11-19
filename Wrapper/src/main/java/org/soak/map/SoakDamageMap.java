package org.soak.map;

import org.spongepowered.api.event.cause.entity.damage.DamageType;

public class SoakDamageMap {

    public static DamageType toSponge(org.bukkit.damage.DamageType type) {
        throw new RuntimeException("Not implemented yet");
    }

    public static org.bukkit.damage.DamageType toBukkit(DamageType type) {
        throw new RuntimeException("Not implemented yet");
    }
}
