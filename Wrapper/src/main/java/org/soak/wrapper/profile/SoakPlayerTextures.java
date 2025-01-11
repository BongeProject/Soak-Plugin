package org.soak.wrapper.profile;

import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.net.URL;
import java.util.Optional;
import java.util.function.Consumer;

public class SoakPlayerTextures implements PlayerTextures {

    Consumer<GameProfile> updateProfile;
    private GameProfile profile;

    public SoakPlayerTextures(GameProfile profile, Consumer<GameProfile> update) {
        this.profile = profile;
        this.updateProfile = update;
    }

    public void set(SoakPlayerTextures texture){
        var opTexture = texture.texture();
        if(opTexture.isEmpty()){
            clear();
            return;
        }
        profile = profile.withProperty(opTexture.get());
        updateProfile.accept(profile);
    }

    private Optional<ProfileProperty> texture() {
        String textureName = org.spongepowered.api.profile.property.ProfileProperty.TEXTURES;
        return this.profile.properties().stream().filter(property -> property.name().equals(textureName)).findAny();
    }

    @Override
    public boolean isEmpty() {
        return texture().isEmpty();
    }

    @Override
    public void clear() {
        profile = profile.withoutProperties(ProfileProperty.TEXTURES);
        updateProfile.accept(profile);
    }

    @Override
    public @Nullable URL getSkin() {
        return null;
    }

    @Override
    public void setSkin(@Nullable URL url) {
        if(url == null){
            clear();
            return;
        }
        if(!url.getHost().equals("textures.minecraft.net")){
            throw new IllegalStateException("Unsupported URL of " + url);
        }
        String value = url.getPath();
        if(value.startsWith("/texture/")) {
            value = value.substring("/texture/".length());
        }
        var prop = ProfileProperty.of(ProfileProperty.TEXTURES, value);
        profile = profile.withProperty(prop);
        updateProfile.accept(profile);
    }

    @Override
    public void setSkin(@Nullable URL url, @Nullable SkinModel skinModel) {
        throw NotImplementedException.createByLazy(PlayerTextures.class, "setSkin", URL.class, SkinModel.class);
    }

    @Override
    public @NotNull SkinModel getSkinModel() {
        throw NotImplementedException.createByLazy(PlayerTextures.class, "getSkinModel");
    }

    @Override
    public @Nullable URL getCape() {
        return null;
    }

    @Override
    public void setCape(@Nullable URL url) {
        throw NotImplementedException.createByLazy(PlayerTextures.class, "setCape", URL.class);
    }

    @Override
    public long getTimestamp() {
        throw NotImplementedException.createByLazy(PlayerTextures.class, "getTimestamp");
    }

    @Override
    public boolean isSigned() {
        return this.texture().map(ProfileProperty::hasSignature).orElse(false);
    }
}
