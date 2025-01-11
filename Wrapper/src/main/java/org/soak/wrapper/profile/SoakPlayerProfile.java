package org.soak.wrapper.profile;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakProfilePropertyMap;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SoakPlayerProfile implements PlayerProfile {

    private @NotNull GameProfile profile;
    private boolean fromCached;

    public SoakPlayerProfile(@NotNull GameProfile profile, boolean cached) {
        this.profile = profile;
        this.fromCached = cached;
    }

    public GameProfile profile(){
        return this.profile;
    }

    @Override
    public @Nullable UUID getUniqueId() {
        return this.profile.uniqueId();
    }

    @Override
    public @Nullable String getName() {
        return this.profile.name().orElse(null);
    }

    @Override
    @Deprecated
    public @NotNull String setName(@Nullable String s) {
        String old = this.profile.name().orElse(null);
        this.profile = this.profile.withName(s);
        return old;
    }

    @Override
    public @Nullable UUID getId() {
        return this.profile.uniqueId();
    }

    @Override
    public @Nullable UUID setId(@Nullable UUID uuid) {
        UUID old = this.profile.uniqueId();
        if (uuid != null) {
            Sponge.server().gameProfileManager().profile(uuid).thenAccept(profile -> {
                this.profile = profile;
            });
        } else if (this.profile.name().isPresent()) {
            Sponge.server().gameProfileManager().profile(this.profile.name().orElseThrow()).thenAccept(profile -> {
                this.profile = profile;
            });
        } else {
            throw new RuntimeException("GameProfile cannot have no name and ID");
        }
        return old;

    }

    @Override
    public @NotNull SoakPlayerTextures getTextures() {
        return new SoakPlayerTextures(this.profile, (profile) -> SoakPlayerProfile.this.profile = profile);
    }

    @Override
    public void setTextures(@Nullable PlayerTextures playerTextures) {
        if(playerTextures == null){
            getTextures().clear();
            return;
        }
        if(!(playerTextures instanceof SoakPlayerTextures spt)){
            throw new IllegalStateException("PlayerTextures must be SoakPlayerTextures: Found " + playerTextures.getClass().getName());
        }
        getTextures().set(spt);
    }

    @Override
    public @NotNull Set<ProfileProperty> getProperties() {
        return this.profile.properties().stream().map(SoakProfilePropertyMap::toBukkit).collect(Collectors.toSet());
    }

    @Override
    public void setProperties(@NotNull Collection<ProfileProperty> collection) {
        this.profile.withoutProperties(collection.stream().map(SoakProfilePropertyMap::toSponge).collect(Collectors.toList()));
    }

    @Override
    public boolean hasProperty(@Nullable String s) {
        return this.profile.properties().stream().anyMatch(property -> property.name().equals(s));
    }

    @Override
    public void setProperty(@NotNull ProfileProperty profileProperty) {
        this.profile = this.profile.withProperty(SoakProfilePropertyMap.toSponge(profileProperty));
    }

    @Override
    public boolean removeProperty(@Nullable String s) {
        if (s == null) {
            return false;
        }
        this.profile = this.profile.withoutProperties(s);
        return true;
    }

    @Override
    public void clearProperties() {
        this.profile = profile.withoutProperties();
    }

    @Override
    public boolean isComplete() {
        return this.profile.hasName();
    }

    @Override
    public boolean completeFromCache() {
        throw NotImplementedException.createByLazy(PlayerProfile.class, "completeFromCache");
    }

    @Override
    public boolean completeFromCache(boolean b) {
        throw NotImplementedException.createByLazy(PlayerProfile.class, "completeFromCache", boolean.class);
    }

    @Override
    public boolean completeFromCache(boolean b, boolean b1) {
        throw NotImplementedException.createByLazy(PlayerProfile.class, "completeFromCache", boolean.class, boolean.class);
    }

    @Override
    public boolean complete(boolean b) {
        throw NotImplementedException.createByLazy(PlayerProfile.class, "complete", boolean.class);
    }

    @Override
    public boolean complete(boolean b, boolean b1) {
        throw NotImplementedException.createByLazy(PlayerProfile.class, "complete", boolean.class, boolean.class);
    }

    @Override
    public @NotNull CompletableFuture<PlayerProfile> update() {
        //profile gets updated
        return CompletableFuture.completedFuture(this);
    }

    @Override
    public @NotNull SoakPlayerProfile clone() {
        return new SoakPlayerProfile(this.profile, this.fromCached);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        var container = this.profile.toContainer();
        return container
                .keys(false)
                .stream()
                .collect(Collectors.toMap(
                        query -> query.asString('.'),
                        query -> container.get(query).orElseThrow(() -> new RuntimeException("No value in '" + query.asString(".") + "'"))));
    }
}
