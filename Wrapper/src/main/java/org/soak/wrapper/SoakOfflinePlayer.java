package org.soak.wrapper;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.util.Ticks;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.WrapperManager;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakLocationMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.entity.living.human.SoakPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.ban.Ban;

import java.time.*;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SoakOfflinePlayer implements OfflinePlayer {

    private final @NotNull CompletableFuture<User> futureUser;
    private @Nullable User user;


    public SoakOfflinePlayer(@NotNull User user) {
        this.user = user;
        this.futureUser = CompletableFuture.completedFuture(user);
    }

    public SoakOfflinePlayer(@NotNull CompletableFuture<User> futureUser) {
        this.futureUser = futureUser;
        futureUser.thenAccept(user -> this.user = user);
    }

    public @NotNull User spongeUser() {
        LocalTime startTime = null;
        while (this.user == null) {
            LocalTime now = LocalTime.now();
            if (startTime == null) {
                startTime = now;
            }
            var duration = Duration.between(startTime, now);
            if (duration.compareTo(Ticks.duration(20)) >= 0) {
                throw new RuntimeException("Time to create user exceeded. Failing");
            }
        }
        return this.user;
    }

    @Override
    public String getName() {
        return this.spongeUser().name();
    }

    @Override
    public boolean isOnline() {
        return this.spongeUser().isOnline();
    }

    @Override
    public boolean isConnected() {
        //TODO find the different between isOnline and isConnected
        return this.spongeUser().isOnline();
    }

    @Override
    public SoakPlayer getPlayer() {
        return this.spongeUser()
                .player()
                .map(spongePlayer -> SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongePlayer))
                .orElse(null);
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.spongeUser().uniqueId();
    }

    @Override
    public @NotNull PlayerProfile getPlayerProfile() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "getPlayerProfile");
    }

    @Override
    public boolean isBanned() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "isBanned");
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Date date, @Nullable String s1) {
        return ban(s, date == null ? null : date.toInstant(), s1);
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Instant instant, @Nullable String s1) {
        var banService = Sponge.server().serviceProvider().banService();
        var user = this.spongeUser();
        var ban = Ban.builder()
                .profile(user.profile())
                .expirationDate(instant)
                .build();
        banService.add(ban);
        return null;
    }

    @Override
    public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String s, @Nullable Duration duration, @Nullable String s1) {
        return ban(s, duration == null ? null : LocalDateTime.now().plus(duration).toInstant(ZoneOffset.UTC), s1);
    }

    @Override
    public boolean isWhitelisted() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "isWhitelisted");
    }

    @Override
    public void setWhitelisted(boolean arg0) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "setWhitelisted", boolean.class);
    }

    @Override
    public long getFirstPlayed() {
        return this.spongeUser().get(Keys.FIRST_DATE_JOINED).map(Instant::getNano).orElse(-1).longValue();
    }

    @Deprecated
    @Override
    public long getLastPlayed() {
        return this.spongeUser().get(Keys.LAST_DATE_PLAYED).map(Instant::getNano).orElse(-1).longValue();
    }

    @Override
    public boolean hasPlayedBefore() {
        return this.spongeUser().get(Keys.FIRST_DATE_JOINED).isPresent();
    }

    @Override
    public Location getBedSpawnLocation() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "getBedSpawnLocation");
    }

    @Override
    public long getLastLogin() {
        return this.spongeUser().get(Keys.LAST_DATE_JOINED).map(Instant::getNano).orElse(-1).longValue();
    }

    @Override
    public long getLastSeen() {
        return this.spongeUser().get(Keys.LAST_DATE_PLAYED).map(Instant::getNano).orElse(-1).longValue();
    }

    @Override
    public @Nullable Location getRespawnLocation() {
        var user = this.spongeUser();
        var spawnPoints = user.get(Keys.RESPAWN_LOCATIONS).orElse(Collections.emptyMap());
        var respawnLocation = spawnPoints.get(user.worldKey());
        if (respawnLocation == null) {
            return null;
        }
        return respawnLocation.asLocation().map(SoakLocationMap::toBukkit).orElse(null);
    }

    @Override
    public void incrementStatistic(Statistic arg0, Material arg1, int arg2) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class,
                "incrementStatistic",
                Statistic.class,
                Material.class,
                int.class);
    }

    @Override
    public void incrementStatistic(Statistic arg0, Material arg1) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class,
                "incrementStatistic",
                Statistic.class,
                Material.class);
    }

    @Override
    public void incrementStatistic(Statistic arg0, EntityType arg1) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class,
                "incrementStatistic",
                Statistic.class,
                EntityType.class);
    }

    @Override
    public void incrementStatistic(Statistic arg0, EntityType arg1, int arg2) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class,
                "incrementStatistic",
                Statistic.class,
                EntityType.class,
                int.class);
    }

    @Override
    public void incrementStatistic(Statistic arg0) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "incrementStatistic", Statistic.class);
    }

    @Override
    public void incrementStatistic(Statistic arg0, int arg1) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class,
                "incrementStatistic",
                Statistic.class,
                int.class);
    }

    @Override
    public void decrementStatistic(Statistic arg0) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "decrementStatistic", Statistic.class);
    }

    @Override
    public void decrementStatistic(Statistic arg0, Material arg1, int arg2) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class,
                "decrementStatistic",
                Statistic.class,
                Material.class,
                int.class);
    }

    @Override
    public void decrementStatistic(Statistic arg0, EntityType arg1) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class,
                "decrementStatistic",
                Statistic.class,
                EntityType.class);
    }

    @Override
    public void decrementStatistic(Statistic arg0, EntityType arg1, int arg2) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class,
                "decrementStatistic",
                Statistic.class,
                EntityType.class,
                int.class);
    }

    @Override
    public void decrementStatistic(Statistic arg0, int arg1) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class,
                "decrementStatistic",
                Statistic.class,
                int.class);
    }

    @Override
    public void decrementStatistic(Statistic arg0, Material arg1) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class,
                "decrementStatistic",
                Statistic.class,
                Material.class);
    }

    @Override
    public void setStatistic(Statistic arg0, EntityType arg1, int arg2) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class,
                "setStatistic",
                Statistic.class,
                EntityType.class,
                int.class);
    }

    @Override
    public @Nullable Location getLastDeathLocation() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "getLastDeathLocation");
    }

    @Override
    public @Nullable Location getLocation() {
        var user = this.spongeUser();
        var worldKey = user.worldKey();
        var world = Bukkit.getWorld(worldKey);
        var position = user.position();
        return new Location(world, position.x(), position.y(), position.z());
    }

    @Override
    public @NotNull PersistentDataContainerView getPersistentDataContainer() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "getPersistentDataContainer");
    }

    @Override
    public void setStatistic(Statistic arg0, int arg1) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "setStatistic", Statistic.class, int.class);
    }

    @Override
    public void setStatistic(Statistic arg0, Material arg1, int arg2) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class,
                "setStatistic",
                Statistic.class,
                Material.class,
                int.class);
    }

    @Override
    public int getStatistic(Statistic arg0, Material arg1) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class,
                "getStatistic",
                Statistic.class,
                Material.class);
    }

    @Override
    public int getStatistic(Statistic arg0) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "getStatistic", Statistic.class);
    }

    @Override
    public int getStatistic(Statistic arg0, EntityType arg1) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class,
                "getStatistic",
                Statistic.class,
                EntityType.class);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "serialize");
    }

    @Override
    public boolean isOp() {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "isOp");
    }

    @Override
    public void setOp(boolean value) {
        throw NotImplementedException.createByLazy(OfflinePlayer.class, "setOp", boolean.class);
    }
}
