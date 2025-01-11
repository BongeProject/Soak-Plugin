package org.soak.wrapper.world.chunk;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.generator.structure.Structure;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.mose.collection.stream.builder.CollectionStreamBuilder;
import org.soak.WrapperManager;
import org.soak.exception.NotImplementedException;
import org.soak.plugin.SoakManager;
import org.soak.utils.ListMappingUtils;
import org.soak.wrapper.block.data.SoakBlockData;
import org.soak.wrapper.block.state.AbstractBlockState;
import org.soak.wrapper.entity.SoakEntity;
import org.soak.wrapper.world.SoakWorld;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.world.chunk.WorldChunk;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.volume.stream.StreamOptions;
import org.spongepowered.math.vector.Vector2i;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AbstractSoakChunk implements SoakChunk {

    private WorldChunk chunk;

    public AbstractSoakChunk(WorldChunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public int getX() {
        return chunk.chunkPosition().x();
    }

    @Override
    public int getZ() {
        return chunk.chunkPosition().z();
    }

    @Override
    public @NotNull SoakWorld getWorld() {
        return SoakManager.<WrapperManager>getManager().getMemoryStore().get((ServerWorld) chunk.world());
    }

    @Override
    public @NotNull Block getBlock(int x, int y, int z) {
        return this.getWorld().getBlockAt(x, y, z);
    }

    @Override
    public @NotNull ChunkSnapshot getChunkSnapshot() {
        return new SoakChunkSnapshot(this.chunk);
    }

    @Override
    public @NotNull ChunkSnapshot getChunkSnapshot(boolean includeMaxBlocky, boolean includeBiome, boolean includeBiomeTempRain) {
        //TODO parameters
        return getChunkSnapshot();
    }

    @Override
    public @NotNull ChunkSnapshot getChunkSnapshot(boolean b, boolean b1, boolean b2, boolean b3) {
        //TODO parameters
        return getChunkSnapshot();
    }

    @Override
    public boolean isEntitiesLoaded() {
        return !this.chunk.entities().isEmpty();
    }

    @Override
    public @NotNull Entity[] getEntities() {
        return this.chunk.entities().stream().map(SoakEntity::wrap).toArray(Entity[]::new);
    }

    @Override
    public @NotNull BlockState[] getTileEntities(boolean useSnapshots) {
        return getBlockEntities((v) -> true, useSnapshots).toArray(BlockState[]::new);
    }

    @Override
    public @NotNull Collection<BlockState> getTileEntities(@NotNull Predicate<? super Block> predicate, boolean useSnapshots) {
        return getBlockEntities(state -> {
            var t = state.getBlock();
            return predicate.test(t);
        }, useSnapshots).collect(Collectors.toList());
    }

    private @NotNull Stream<BlockState> getBlockEntities(@NotNull Predicate<AbstractBlockState> predicate, boolean useSnapshots) {
        return this
                .chunk
                .blockEntities()
                .stream()
                .map(blockEntity -> (AbstractBlockState) AbstractBlockState.wrap(blockEntity.serverLocation(), blockEntity.block(), useSnapshots))
                .filter(predicate)
                .map(state -> state);
    }

    @Override
    public boolean isGenerated() {
        return true;
    }

    @Override
    public boolean isLoaded() {
        return this.getWorld().isChunkLoaded(this);
    }

    @Override
    public boolean load(boolean generate) {
        var opWorldChunk = this.chunk.world().loadChunk(getX(), 0, getZ(), generate);
        if (opWorldChunk.isEmpty()) {
            return false;
        }
        this.chunk = opWorldChunk.get();
        return true;
    }

    @Override
    public boolean load() {
        return load(false);
    }

    @Override
    public boolean unload(boolean save) {
        throw NotImplementedException.createByLazy(Chunk.class, "unload", boolean.class);
    }

    @Override
    public boolean unload() {
        return unload(true);
    }

    @Override
    public boolean isSlimeChunk() {
        throw NotImplementedException.createByLazy(Chunk.class, "isSlimeChunk");
    }

    @Override
    public boolean isForceLoaded() {
        throw NotImplementedException.createByLazy(Chunk.class, "isForceLoaded");
    }

    @Override
    public void setForceLoaded(boolean b) {
        throw NotImplementedException.createByLazy(Chunk.class, "setForceLoaded", boolean.class);
    }

    public Vector2i getVectorPos() {
        return Vector2i.from(getX(), getZ());
    }

    @Override
    public boolean addPluginChunkTicket(@NotNull Plugin plugin) {
        return getWorld().addKeepAliveTicket(getVectorPos(), plugin);
    }

    @Override
    public boolean removePluginChunkTicket(@NotNull Plugin plugin) {
        return getWorld().removeKeepAliveTicket(getVectorPos(), plugin);
    }

    @Override
    public @NotNull Collection<Plugin> getPluginChunkTickets() {
        return getWorld().getKeepAliveTickets(getVectorPos());
    }

    @Override
    public long getInhabitedTime() {
        return this.chunk.inhabitedTime().ticks();
    }

    @Override
    public void setInhabitedTime(long ticks) {
        this.chunk.setInhabitedTime(Ticks.of(ticks));
    }

    @Override
    public boolean contains(@NotNull BlockData blockData) {
        var max = this.chunk.max();
        var min = this.chunk.min();
        var blockState = ((SoakBlockData) blockData).sponge();
        return this
                .chunk
                .blockStateStream(
                        min,
                        max,
                        StreamOptions
                                .builder()
                                .setLoadingStyle(StreamOptions.LoadingStyle.FORCED_GENERATED)
                                .setCarbonCopy(true)
                                .build())
                .anyMatch(worldChunkVolumeElement -> chunk
                        .block(worldChunkVolumeElement.position().toInt()).equals(blockState));
    }

    @Override
    public boolean contains(@NotNull Biome biome) {
        throw NotImplementedException.createByLazy(Chunk.class, "contains", Biome.class);
    }

    @NotNull
    @Override
    public LoadLevel getLoadLevel() {
        throw NotImplementedException.createByLazy(Chunk.class, "getLoadLevel");
    }

    @Override
    public @NotNull Collection<GeneratedStructure> getStructures() {
        throw NotImplementedException.createByLazy(Chunk.class, "getStructures");
    }

    @Override
    public @NotNull Collection<GeneratedStructure> getStructures(@NotNull Structure structure) {
        throw NotImplementedException.createByLazy(Chunk.class, "getStructures", Structure.class);
    }

    @Override
    public @NotNull Collection<Player> getPlayersSeeingChunk() {
        var chunkPosition = this.chunk.chunkPosition();
        var chunks = this.chunk.world().players().stream().filter(player -> {
            var viewDistance = player.get(Keys.VIEW_DISTANCE).orElse(2);
            var playerChunkPosition = player.serverLocation().chunkPosition();
            return playerChunkPosition.distanceSquared(chunkPosition) <= viewDistance;
        }).map(player -> (ServerPlayer)player);

        return CollectionStreamBuilder.builder().stream(chunks).basicMap(player -> (Player)SoakManager.<WrapperManager>getManager().getMemoryStore().get(player)).buildSet();
    }

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        throw NotImplementedException.createByLazy(Chunk.class, "getPersistentDataContainer");
    }
}
