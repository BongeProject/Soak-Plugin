package org.soak.wrapper.world.chunk;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.soak.WrapperManager;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakBiomeMap;
import org.soak.plugin.SoakManager;
import org.spongepowered.api.world.LightTypes;
import org.spongepowered.api.world.chunk.ChunkStates;
import org.spongepowered.api.world.chunk.WorldChunk;
import org.spongepowered.api.world.server.ServerWorld;

/*
this is meant to be used for async block reading ....

Minecraft does allow for reading of block data (not BlockEntityData) asynced,
this is extended into Sponge (I believe only Bukkit platforms block it) so ....

just wrap the WorldChunk again????
 */
public class SoakChunkSnapshot implements ChunkSnapshot {

    private final WorldChunk chunk;

    SoakChunkSnapshot(WorldChunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public int getX() {
        return this.chunk.chunkPosition().x();
    }

    @Override
    public int getZ() {
        return this.chunk.chunkPosition().z();
    }

    @Override
    public @NotNull String getWorldName() {
        return ((ServerWorld) this.chunk.world()).key().value();
    }

    @Override
    public @NotNull Material getBlockType(int x, int y, int z) {
        return getBlockData(x, y, z).getMaterial();
    }

    @Override
    public @NotNull BlockData getBlockData(int x, int y, int z) {
        var spongeBlockState = this.chunk.block(x, y, z);
        return SoakManager.<WrapperManager>getManager().getMemoryStore().get(spongeBlockState);

    }

    @Override
    @Deprecated
    public int getData(int i, int i1, int i2) {
        throw NotImplementedException.createByLazy(SoakChunkSnapshot.class, "getData", int.class, int.class, int.class);
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return this.chunk.world().light(LightTypes.SKY, x, y, z);
    }

    @Override
    public int getBlockEmittedLight(int x, int y, int z) {
        return this.chunk.world().light(LightTypes.BLOCK, x, y, z);

    }

    @Override
    public int getHighestBlockYAt(int x, int z) {
        return this.chunk.highestYAt(x, z);
    }

    @Override
    @Deprecated
    public @NotNull Biome getBiome(int i, int i1) {
        throw NotImplementedException.createByLazy(SoakChunkSnapshot.class, "getBiome", int.class, int.class);
    }

    @Override
    public @NotNull Biome getBiome(int x, int y, int z) {
        var minBlock = this.chunk.min();
        var spongeBiome = this.chunk.biome(x + minBlock.x(), y, z + minBlock.y());
        return SoakBiomeMap.toBukkit(spongeBiome);
    }

    @Override
    @Deprecated
    public double getRawBiomeTemperature(int i, int i1) {
        throw NotImplementedException.createByLazy(SoakChunkSnapshot.class, "getRawBiomeTemperature", int.class, int.class);
    }

    @Override
    public double getRawBiomeTemperature(int i, int i1, int i2) {
        throw NotImplementedException.createByLazy(SoakChunkSnapshot.class, "getRawBiomeTemperature", int.class, int.class, int.class);
    }

    @Override
    public long getCaptureFullTime() {
        throw NotImplementedException.createByLazy(SoakChunkSnapshot.class, "getCaptureFullTime", long.class);
    }

    @Override
    public boolean isSectionEmpty(int i) {
        //TODO check section
        return this.chunk.state() == ChunkStates.EMPTY.get();
    }

    @Override
    public boolean contains(@NotNull BlockData blockData) {
        throw NotImplementedException.createByLazy(SoakChunkSnapshot.class, "contains", BlockData.class);
    }

    @Override
    public boolean contains(@NotNull Biome biome) {
        throw NotImplementedException.createByLazy(SoakChunkSnapshot.class, "contains", Biome.class);
    }
}
