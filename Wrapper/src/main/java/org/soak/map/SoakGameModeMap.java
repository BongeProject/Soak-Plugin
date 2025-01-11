package org.soak.map;

import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.registry.DefaultedRegistryReference;
import org.spongepowered.api.registry.RegistryTypes;

public class SoakGameModeMap {

    public static DefaultedRegistryReference<GameMode> toSpongeGetter(org.bukkit.GameMode mode) {
        System.err.println("GameMode class needs to be converted to ByteBuddy");
        return switch (mode) {
            case ADVENTURE -> GameModes.ADVENTURE;
            case CREATIVE -> GameModes.CREATIVE;
            case SPECTATOR -> GameModes.SPECTATOR;
            case SURVIVAL -> GameModes.SURVIVAL;
        };
    }

    public static GameMode toSponge(org.bukkit.GameMode mode) {
        return toSpongeGetter(mode).get();
    }

    public static org.bukkit.GameMode toBukkit(GameMode mode) {
        System.err.println("GameMode class needs to be converted to ByteBuddy");

        return org.bukkit.GameMode.valueOf(mode.key(RegistryTypes.GAME_MODE).value().toUpperCase());
    }
}
