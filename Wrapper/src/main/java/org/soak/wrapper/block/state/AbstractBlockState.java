package org.soak.wrapper.block.state;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.soak.WrapperManager;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakBlockMap;
import org.soak.map.SoakLocationMap;
import org.soak.plugin.SoakManager;
import org.soak.utils.KeyValuePair;
import org.soak.wrapper.block.SoakBlock;
import org.soak.wrapper.block.data.AbstractBlockData;
import org.soak.wrapper.block.data.SoakBlockData;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public abstract class AbstractBlockState implements BlockState {

    private final LinkedBlockingQueue<KeyValuePair<?>> toApply = new LinkedBlockingQueue<>();
    private @Nullable ServerLocation location;
    private @NotNull org.spongepowered.api.block.BlockState state;

    public AbstractBlockState(@NotNull ServerLocation location) {
        this(location, location.block());
    }

    public AbstractBlockState(@Nullable ServerLocation location, @NotNull org.spongepowered.api.block.BlockState state) {
        this.location = location;
        this.state = state;
    }

    public static AbstractBlockState wrap(@Nullable ServerLocation location, org.spongepowered.api.block.BlockState state, boolean isSnapshot) {
        if (state.get(Keys.SIGN_WAXED).isPresent()) {
            return new SignBlockState(location, state, isSnapshot);
        }
        return new BasicBlockState(location, state);
    }

    protected abstract AbstractBlockState createCopy(@Nullable ServerLocation location, @NotNull org.spongepowered.api.block.BlockState state);

    protected abstract void onPostApply(@NotNull ServerLocation location);

    public @Nullable ServerLocation location() {
        return this.location;
    }

    public @NotNull org.spongepowered.api.block.BlockState state() {
        return this.state;
    }

    @Override
    public @NotNull Block getBlock() {
        return new SoakBlock(this.location);
    }

    @Override
    public @NotNull MaterialData getData() {
        throw NotImplementedException.createByLazy(BlockState.class, "getData");
    }

    @Override
    public void setData(@NotNull MaterialData materialData) {
        throw NotImplementedException.createByLazy(BlockState.class, "setData", MaterialData.class);
    }

    @Override
    public @NotNull BlockData getBlockData() {
        return SoakBlockData.internalCreateBlockData(this.state);
    }

    @Override
    public void setBlockData(@NotNull BlockData blockData) {
        this.state = ((AbstractBlockData) blockData).sponge();
    }

    @Override
    public @NotNull BlockState copy() {
        return copy(location);
    }

    @Override
    public @NotNull BlockState copy(@NotNull Location location) {
        return copy(SoakLocationMap.toSponge(location));
    }

    public @NotNull BlockState copy(@Nullable ServerLocation location) {
        var copy = createCopy(location, this.state);
        this.toApply.stream().map(KeyValuePair::clone).collect(Collectors.toCollection(() -> copy.toApply));
        return copy;
    }

    public <Val> KeyValuePair<Val> key(Key<? extends Value<Val>> key) {
        return (KeyValuePair<Val>) this.toApply.stream().filter(pair -> pair.getKey().equals(key)).findAny().orElseThrow();
    }

    @Override
    public @NotNull Material getType() {
        return SoakBlockMap.toBukkit(this.state.type());
    }

    @Override
    public void setType(@NotNull Material material) {
        setBlockData(material.createBlockData());
    }

    @Override
    public byte getLightLevel() {
        return this.state.get(Keys.BLOCK_LIGHT).orElseThrow(() -> new RuntimeException("Probably wrong key")).byteValue();
    }

    @Override
    public @NotNull World getWorld() {
        return SoakManager.<WrapperManager>getManager().getMemoryStore().get(this.location.world());
    }

    @Override
    public int getX() {
        return this.location.blockX();
    }

    @Override
    public int getY() {
        return this.location.blockY();
    }

    @Override
    public int getZ() {
        return this.location.blockZ();
    }

    @Override
    public @NotNull Location getLocation() {
        return SoakLocationMap.toBukkit(this.location);
    }

    @Override
    public @Nullable Location getLocation(@Nullable Location location) {
        throw NotImplementedException.createByLazy(BlockState.class, "getLocation", Location.class);
    }

    @Override
    public @NotNull Chunk getChunk() {
        var chunkPosition = this.location.chunkPosition();
        return getWorld().getChunkAt(chunkPosition.x(), chunkPosition.z());
    }

    @Override
    public boolean update() {
        return update(false, true);
    }

    @Override
    public boolean update(boolean force) {
        return update(false, true);
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        if (!force && !isPlaced()) {
            return false;
        }
        if (this.location == null) {
            return false;
        }
        this.toApply.forEach(pair -> pair.apply(this.location));
        onPostApply(this.location);
        return true;
    }

    @Override
    public byte getRawData() {
        return 0;
    }

    @Override
    public void setRawData(byte b) {

    }

    @Override
    public boolean isPlaced() {
        return this.location.block().type().equals(this.state.type());
    }

    @Override
    public boolean isCollidable() {
        throw NotImplementedException.createByLazy(BlockState.class, "isCollidable");
    }

    @Override
    public @Unmodifiable @NotNull Collection<ItemStack> getDrops(@Nullable ItemStack itemStack, @Nullable Entity entity) {
        return List.of();
    }

    @Override
    public void setMetadata(@NotNull String metadataKey, @NotNull MetadataValue newMetadataValue) {
        throw NotImplementedException.createByLazy(AbstractBlockState.class,
                "setMetadata",
                String.class,
                MetadataValue.class);
    }

    @Override
    public @NotNull List<MetadataValue> getMetadata(@NotNull String metadataKey) {
        throw NotImplementedException.createByLazy(AbstractBlockState.class, "getMetadata", String.class);
    }

    @Override
    public boolean hasMetadata(@NotNull String metadataKey) {
        throw NotImplementedException.createByLazy(AbstractBlockState.class, "hasMetadata", String.class);
    }

    @Override
    public void removeMetadata(@NotNull String metadataKey, @NotNull Plugin owningPlugin) {
        throw NotImplementedException.createByLazy(AbstractBlockState.class, "metadataKey", String.class, Plugin.class);
    }
}
