package org.soak.map;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.cause.entity.SpawnType;
import org.spongepowered.api.event.cause.entity.SpawnTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.registry.DefaultedRegistryReference;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.Map;
import java.util.function.Predicate;

public class SoakSpawnReasonMap {

    public static Map<CreatureSpawnEvent.SpawnReason, Predicate<Cause>> toBukkit(SpawnType type) {
        if (SpawnTypes.SPAWN_EGG.get().equals(type)) {
            return Map.of(
                    CreatureSpawnEvent.SpawnReason.EGG,
                    cause -> cause
                            .first(Item.class)
                            .map(item -> item.item().get().type().equals(ItemTypes.EGG.get()))
                            .orElse(false),
                    CreatureSpawnEvent.SpawnReason.SPAWNER_EGG,
                    cause -> cause
                            .first(Item.class)
                            .map(item -> {
                                var itemType = item.item().get().type();
                                return !itemType.equals(ItemTypes.EGG.get());
                            }).orElse(false));
        }
        throw new RuntimeException("Unknown SpawnType of " + type.key(RegistryTypes.SPAWN_TYPE).asString());
    }

    public static CreatureSpawnEvent.SpawnReason toBukkit(SpawnType type, Cause cause) {
        return toBukkit(type)
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().test(cause))
                .map(Map.Entry::getKey)
                .findAny()
                .orElseThrow(() -> new RuntimeException("Unknown spawn type and cause combo"));
    }

    public static DefaultedRegistryReference<SpawnType> toSponge(CreatureSpawnEvent.SpawnReason reason) {
        return switch (reason) {
            case BEEHIVE, SPAWNER, TRIAL_SPAWNER -> SpawnTypes.MOB_SPAWNER;
            case EGG, SPAWNER_EGG -> SpawnTypes.SPAWN_EGG;
            case BREEDING, OCELOT_BABY -> SpawnTypes.BREEDING;
            case BUILD_IRONGOLEM, BUILD_SNOWMAN, BUILD_WITHER, SILVERFISH_BLOCK -> SpawnTypes.BLOCK_SPAWNING;
            case ENCHANTMENT, OMINOUS_ITEM_SPAWNER, DEFAULT, POTION_EFFECT -> SpawnTypes.WORLD_SPAWNER;
            case COMMAND, MOUNT, CURED, TRAP, RAID, CUSTOM, DROWNED, DUPLICATION, ENDER_PEARL, FROZEN, INFECTION,
                 JOCKEY, LIGHTNING, METAMORPHOSIS, NATURAL, NETHER_PORTAL, PATROL, PIGLIN_ZOMBIFIED, REINFORCEMENTS,
                 SHEARED, SHOULDER_ENTITY, SLIME_SPLIT, SPELL, VILLAGE_DEFENSE, VILLAGE_INVASION -> SpawnTypes.CUSTOM;
            case DISPENSE_EGG -> SpawnTypes.DISPENSE;
            case EXPLOSION -> SpawnTypes.TNT_IGNITE;
            case CHUNK_GEN -> SpawnTypes.CHUNK_LOAD;
        };
    }

}
