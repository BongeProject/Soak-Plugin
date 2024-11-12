package org.soak.wrapper.entity;

import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.WrapperManager;
import org.soak.exception.NotImplementedException;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.weather.LightningBolt;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Ticks;

import java.util.UUID;

public class SoakLightningStrike extends AbstractEntity<LightningBolt> implements LightningStrike {

    public SoakLightningStrike(LightningBolt bolt) {
        this(Sponge.systemSubject(), Sponge.systemSubject(), bolt);
    }


    public SoakLightningStrike(Subject subject, Audience audience, LightningBolt entity) {
        super(subject, audience, entity);
    }

    @Override
    public SpigotLightningStrike spigot() {
        return new SpigotLightningStrike(new SoakSpigotEntity(this));
    }

    @Override
    public boolean isEffect() {
        return this.spongeEntity().get(Keys.IS_EFFECT_ONLY).orElse(false);
    }

    @Override
    public int getFlashes() {
        throw NotImplementedException.createByLazy(LightningStrike.class, "getFlashes");
    }

    @Override
    public void setFlashes(int i) {
        throw NotImplementedException.createByLazy(LightningStrike.class, "setFlashes", int.class);
    }

    @Override
    public int getFlashCount() {
        throw NotImplementedException.createByLazy(LightningStrike.class, "getFlashCount");
    }

    @Override
    public void setFlashCount(int i) {
        throw NotImplementedException.createByLazy(LightningStrike.class, "setFlashCount", int.class);

    }

    @Override
    public int getLifeTicks() {
        return this.spongeEntity().get(Keys.DESPAWN_DELAY).map(ticks -> (int) ticks.ticks()).orElse(-1);
    }

    @Override
    public void setLifeTicks(int i) {
        if (i == -1) {
            this.spongeEntity().remove(Keys.DESPAWN_DELAY);
            return;
        }
        this.spongeEntity().offer(Keys.DESPAWN_DELAY, Ticks.of(i));
    }

    @Override
    public @Nullable Player getCausingPlayer() {
        var opCreator = this.spongeEntity().creator();
        return opCreator.flatMap(mut -> Sponge.server().player(mut.get())).map(player -> SoakManager.<WrapperManager>getManager().getMemoryStore().get(player)).orElse(null);
    }

    @Override
    public void setCausingPlayer(@Nullable Player player) {
        throw NotImplementedException.createByLazy(LightningStrike.class, "setCausingPlayer", Player.class);

    }

    @Override
    public @Nullable Entity getCausingEntity() {
        throw NotImplementedException.createByLazy(LightningStrike.class, "getCausingEntity");
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public class SpigotLightningStrike extends LightningStrike.Spigot {

        private SoakSpigotEntity spigotEntity;

        SpigotLightningStrike(SoakSpigotEntity entity) {
            this.spigotEntity = entity;
        }

        @Override
        public boolean isSilent() {
            return SoakLightningStrike.this.isSilent();
        }

        @Override
        public void sendMessage(@NotNull BaseComponent component) {
            this.spigotEntity.sendMessage(component);
        }

        @Override
        public void sendMessage(@NotNull BaseComponent... components) {
            this.spigotEntity.sendMessage(components);
        }

        @Override
        public void sendMessage(@Nullable UUID sender, @NotNull BaseComponent component) {
            this.spigotEntity.sendMessage(sender, component);
        }

        @Override
        public void sendMessage(@Nullable UUID sender, @NotNull BaseComponent... components) {
            this.spigotEntity.sendMessage(sender, components);
        }
    }
}
