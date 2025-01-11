package org.soak.wrapper.entity.living.human.user;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.WrapperManager;
import org.soak.exception.NotImplementedException;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.profile.SoakPlayerProfile;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.profile.GameProfile;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Deprecated(forRemoval = true)
public class SoakLoadingUser implements OfflinePlayer {

    private final GameProfile profile;
    private @Nullable SoakLoadedUser loaded;

    public SoakLoadingUser(@NotNull GameProfile profile) {
        this.profile = profile;
        Sponge.server().userManager().loadOrCreate(profile.uuid()).thenAccept(user -> {
            loaded = new SoakLoadedUser(user);
        });
    }

    private <T> T checkLoadedSimple(Function<SoakLoadedUser, T> run, T value) {
        return checkLoaded(run, profile -> value);
    }

    private <T> T checkLoaded(Function<SoakLoadedUser, T> run, Function<GameProfile, T> elser) {
        if (this.loaded == null) {
            return elser.apply(this.profile);
        }
        return run.apply(this.loaded);
    }

    @Override
    public boolean isOnline() {
        return checkLoadedSimple(SoakLoadedUser::isOnline, false);
    }

    @Override
    public boolean isConnected() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "isConnected");
    }

    @Override
    public @Nullable String getName() {
        return profile.name().orElse(null);
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return profile.uniqueId();
    }

    @Override
    public @NotNull PlayerProfile getPlayerProfile() {
        return new SoakPlayerProfile(this.profile, true);
    }

    @Override
    public boolean isBanned() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "isBanned");
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Date date, @Nullable String s1) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "ban", String.class, Date.class, String.class);
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Instant instant, @Nullable String s1) {
              throw NotImplementedException.createByLazy(OfflinePlayer.class, "ban", String.class, Instant.class, String.class);
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Duration duration, @Nullable String s1) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "ban", String.class, Duration.class, String.class);
    }

    @Override
    public boolean isWhitelisted() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "isWhitelisted");
    }

    @Override
    public void setWhitelisted(boolean b) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "isWhitelisted", boolean.class);
    }

    @Override
    public @Nullable Player getPlayer() {
        return Sponge
                .server()
                .onlinePlayers()
                .stream()
                .filter(player -> player.profile().equals(this.profile))
                .findAny()
                .map(player -> SoakManager.<WrapperManager>getManager().getMemoryStore().get(player))
                .orElse(null);
    }

    @Override
    public long getFirstPlayed() {
        return checkLoadedSimple(SoakLoadedUser::getFirstPlayed, 0L);
    }

    @Override
    public long getLastPlayed() {
        return checkLoadedSimple(SoakLoadedUser::getLastPlayed, 0L);
    }

    @Override
    public boolean hasPlayedBefore() {
        return checkLoadedSimple(SoakLoadedUser::hasPlayedBefore, false);
    }

    @Override
    public @Nullable Location getBedSpawnLocation() {
        return checkLoadedSimple(SoakLoadedUser::getBedSpawnLocation, null);
    }

    @Override
    public long getLastLogin() {
        return this.getLastPlayed();
    }

    @Override
    public long getLastSeen() {
        return this.getLastPlayed();
    }

    @Override
    public @Nullable Location getRespawnLocation() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "getRespawnLocation");
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "incrementStatistic", Statistic.class);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "decrementStatistic", Statistic.class);

    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, int i) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "incrementStatistic", Statistic.class, int.class);

    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, int i) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "decrementStatistic", Statistic.class, int.class);

    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, int i) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "setStatistic", Statistic.class, int.class);
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "getStatistic", Statistic.class);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "incrementStatistic", Statistic.class, Material.class);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "decrementStatistic", Statistic.class, Material.class);
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "getStatistic", Statistic.class, Material.class);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull Material material, int i) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "incrementStatistic", Statistic.class, Material.class, int.class);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull Material material, int i) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "decrementStatistic", Statistic.class, Material.class, int.class);
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, @NotNull Material material, int i) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "setStatistic", Statistic.class, Material.class, int.class);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "incrementStatistic", Statistic.class, EntityType.class);

    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "decrementStatistic", Statistic.class, EntityType.class);
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "getStatistic", Statistic.class, EntityType.class);
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int i) throws IllegalArgumentException {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "incrementStatistic", Statistic.class, EntityType.class, int.class);
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int i) {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "decrementStatistic", Statistic.class);
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int i) {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "setStatistic", Statistic.class, EntityType.class, int.class);
    }

    @Override
    public @Nullable Location getLastDeathLocation() {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "getLastDeathLocation");
    }

    @Override
    public @Nullable Location getLocation() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "getLocation");
    }

    @Override
    public @NotNull PersistentDataContainerView getPersistentDataContainer() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "getPersistentDataContainer");
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "serialize");
    }

    @Override
    public boolean isOp() {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "isOp");
    }

    @Override
    public void setOp(boolean b) {
        throw NotImplementedException.createByLazy(SoakLoadedUser.class, "setOp", boolean.class);
    }
}
