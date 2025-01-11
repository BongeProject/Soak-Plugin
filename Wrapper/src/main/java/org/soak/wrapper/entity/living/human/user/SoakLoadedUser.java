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
import org.soak.map.SoakLocationMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.profile.SoakPlayerProfile;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.util.RespawnLocation;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Deprecated(forRemoval = true)
public class SoakLoadedUser implements OfflinePlayer {

    private final User user;

    public SoakLoadedUser(@NotNull User user) {
        this.user = user;
    }

    @Override
    public boolean isOnline() {
        return user.isOnline();
    }

    @Override
    public boolean isConnected() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "isConnected");
    }

    @Override
    public @Nullable Location getRespawnLocation() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "getRespawnLocation");
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
    public @Nullable String getName() {
        return user.name();
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return user.uniqueId();
    }

    @Override
    public @NotNull PlayerProfile getPlayerProfile() {
        return new SoakPlayerProfile(user.profile(), true);
    }

    @Override
    public boolean isBanned() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "isBanned");
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
        return user.player().map(player -> SoakManager.<WrapperManager>getManager().getMemoryStore().get(player)).orElse(null);
    }

    @Override
    public long getFirstPlayed() {
        return user.get(Keys.FIRST_DATE_JOINED).map(Instant::getNano).orElse(0);
    }

    @Override
    public long getLastPlayed() {
        return user.get(Keys.LAST_DATE_JOINED).map(Instant::getNano).orElse(0);
    }

    @Override
    public boolean hasPlayedBefore() {
        return user.get(Keys.FIRST_DATE_JOINED).isPresent();
    }

    @Override
    public @Nullable Location getBedSpawnLocation() {
        return user
                .get(Keys.RESPAWN_LOCATIONS)
                .map(map -> map.get(user.worldKey()))
                .flatMap(RespawnLocation::asLocation)
                .map(SoakLocationMap::toBukkit)
                .orElse(null);
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
