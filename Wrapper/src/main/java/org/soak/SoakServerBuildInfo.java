package org.soak;

import io.papermc.paper.ServerBuildInfo;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.soak.exception.NotImplementedException;

import java.time.Instant;
import java.util.Optional;
import java.util.OptionalInt;

@SuppressWarnings("NonExtendableApiUsage")
public class SoakServerBuildInfo implements ServerBuildInfo {
    @Override
    public Key brandId() {
        return Key.key("soak", "sponge");
    }

    @Override
    public boolean isBrandCompatible(Key key) {
        throw NotImplementedException.createByLazy(ServerBuildInfo.class, "isBrandCompatible", Key.class);
    }

    @Override
    public String brandName() {
        return "soak";
    }

    @Override
    public String minecraftVersionId() {
        return Bukkit.getMinecraftVersion();
    }

    @Override
    public String minecraftVersionName() {
        return Bukkit.getMinecraftVersion();
    }

    @Override
    public OptionalInt buildNumber() {
        return OptionalInt.empty();
    }

    @Override
    public Instant buildTime() {
        throw NotImplementedException.createByLazy(ServerBuildInfo.class, "buildTime");
    }

    @Override
    public Optional<String> gitBranch() {
        return Optional.empty();
    }

    @Override
    public Optional<String> gitCommit() {
        return Optional.empty();
    }

    @Override
    public String asString(StringRepresentation stringRepresentation) {
        return switch (stringRepresentation) {
            case VERSION_SIMPLE -> minecraftVersionId();
            case VERSION_FULL -> "soak:" + minecraftVersionId();
        };
    }
}
