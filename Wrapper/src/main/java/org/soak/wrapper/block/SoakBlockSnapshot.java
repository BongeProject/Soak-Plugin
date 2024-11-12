package org.soak.wrapper.block;

import com.destroystokyo.paper.block.BlockSoundGroup;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundGroup;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.util.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.soak.WrapperManager;
import org.soak.exception.NotImplementedException;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.data.CommonBlockData;
import org.soak.wrapper.block.state.AbstractBlockState;
import org.soak.wrapper.world.SoakWorld;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.math.vector.Vector3i;

import java.util.Collection;
import java.util.List;

public class SoakBlockSnapshot extends AbstractBlock<BlockSnapshot> {

    public SoakBlockSnapshot(BlockSnapshot spongeBlock) {
        super(spongeBlock);
    }

    @Override
    public org.spongepowered.api.block.BlockState spongeBlockState() {
        return sponge().state();
    }

    @Override
    public Vector3i spongePosition() {
        return this.sponge().position();
    }

    @Deprecated
    @Override
    public byte getData() {
        throw NotImplementedException.createByLazy(Block.class, "getData");
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(Block.class, "getDestroySpeed", ItemStack.class, boolean.class);
    }

    @Override
    public void setBlockData(@NotNull BlockData arg0) {
        if (!(arg0 instanceof CommonBlockData)) {
            throw new RuntimeException(arg0.getClass().getName() + " does not implement CommonBlockData");
        }
        this.block = this.block.withState(((CommonBlockData) arg0).sponge());
    }

    @Override
    public @NotNull SoakWorld getWorld() {
        var worldKey = this.block.world();
        var opWorld = Sponge.server().worldManager().world(worldKey);
        return opWorld.map(world -> SoakManager.<WrapperManager>getManager().getMemoryStore().get(world))
                .orElseThrow(() -> new IllegalStateException("Plugin attempted to access a block in a world (" + worldKey.formatted() + ") that isn't loaded"));
    }

    @Override
    public boolean isValidTool(ItemStack arg0) {
        throw NotImplementedException.createByLazy(Block.class, "isValidTool", ItemStack.class);
    }

    @Override
    public void setBlockData(BlockData arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(Block.class, "setBlockData", BlockData.class, boolean.class);
    }

    @Override
    public void setType(Material arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(Block.class, "setType", Material.class, boolean.class);
    }

    @Override
    public BlockFace getFace(Block arg0) {
        throw NotImplementedException.createByLazy(Block.class, "getFace", Block.class);
    }

    @Override
    public Biome getBiome() {
        throw NotImplementedException.createByLazy(Block.class, "getBiome");
    }

    @Override
    public void setBiome(Biome arg0) {
        throw NotImplementedException.createByLazy(Block.class, "setBiome", Biome.class);
    }

    @Override
    public boolean isBlockFacePowered(BlockFace arg0) {
        throw NotImplementedException.createByLazy(Block.class, "isBlockFacePowered", BlockFace.class);
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(BlockFace arg0) {
        throw NotImplementedException.createByLazy(Block.class, "isBlockFaceIndirectlyPowered", BlockFace.class);
    }

    @Override
    public int getBlockPower(BlockFace arg0) {
        throw NotImplementedException.createByLazy(Block.class, "getBlockPower", BlockFace.class);
    }

    @Override
    public boolean isLiquid() {
        throw NotImplementedException.createByLazy(Block.class, "isLiquid");
    }

    @Override
    public boolean isBuildable() {
        throw NotImplementedException.createByLazy(Block.class, "isBuildable");
    }

    @Override
    public boolean isBurnable() {
        throw NotImplementedException.createByLazy(Block.class, "isBurnable");
    }

    @Override
    public boolean isReplaceable() {
        throw NotImplementedException.createByLazy(Block.class, "isReplaceable");
    }

    @Override
    public double getHumidity() {
        throw NotImplementedException.createByLazy(Block.class, "getHumidity");
    }

    @Override
    public boolean breakNaturally(ItemStack arg0, boolean arg1) {
        throw NotImplementedException.createByLazy(Block.class, "breakNaturally", ItemStack.class, boolean.class);
    }

    @Override
    public void tick() {
        //cant be done on snapshot
    }

    @Override
    public void fluidTick() {
        //cant be done on snapshot
    }

    @Override
    public void randomTick() {
//cant be done on snapshot
    }

    @Override
    public boolean breakNaturally() {
        throw NotImplementedException.createByLazy(Block.class, "breakNaturally");
    }

    @Override
    public boolean breakNaturally(ItemStack arg0) {
        throw NotImplementedException.createByLazy(Block.class, "breakNaturally", ItemStack.class);
    }

    @Override
    public boolean applyBoneMeal(BlockFace arg0) {
        throw NotImplementedException.createByLazy(Block.class, "applyBoneMeal", BlockFace.class);
    }

    @Override
    public Collection getDrops(ItemStack arg0, Entity arg1) {
        throw NotImplementedException.createByLazy(Block.class, "getDrops", ItemStack.class, Entity.class);
    }

    @Override
    public Collection getDrops() {
        throw NotImplementedException.createByLazy(Block.class, "getDrops");
    }

    @Override
    public Collection getDrops(ItemStack arg0) {
        throw NotImplementedException.createByLazy(Block.class, "getDrops", ItemStack.class);
    }

    @Override
    public float getBreakSpeed(@NotNull Player player) {
        throw NotImplementedException.createByLazy(Block.class, "getBreakSpeed", Player.class);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape() {
        throw NotImplementedException.createByLazy(Block.class, "getCollisionShape");
    }

    @Override
    public @NotNull SoundGroup getBlockSoundGroup() {
        throw NotImplementedException.createByLazy(Block.class, "getBlockSoundGroup");
    }

    @Override
    public boolean canPlace(@NotNull BlockData blockData) {
        throw NotImplementedException.createByLazy(Block.class, "canPlace", BlockData.class);
    }

    @Override
    public boolean isCollidable() {
        throw NotImplementedException.createByLazy(Block.class, "isCollidable");
    }

    @Override
    public boolean breakNaturally(boolean b, boolean b1) {
        throw NotImplementedException.createByLazy(Block.class, "breakNaturally", boolean.class, boolean.class);
    }

    @Override
    public @NotNull Biome getComputedBiome() {
        throw NotImplementedException.createByLazy(Block.class, "getComputedBiome");
    }

    @Override
    public boolean breakNaturally(@NotNull ItemStack itemStack, boolean b, boolean b1) {
        throw NotImplementedException.createByLazy(Block.class, "breakNaturally", ItemStack.class, boolean.class, boolean.class);
    }

    @Override
    public boolean isPassable() {
        throw NotImplementedException.createByLazy(Block.class, "isPassable");
    }

    @Override
    public BoundingBox getBoundingBox() {
        throw NotImplementedException.createByLazy(Block.class, "getBoundingBox");
    }

    @Override
    public BlockSoundGroup getSoundGroup() {
        throw NotImplementedException.createByLazy(Block.class, "getSoundGroup");
    }

    @Override
    public String getTranslationKey() {
        throw NotImplementedException.createByLazy(Block.class, "getTranslationKey");
    }

    @Override
    public RayTraceResult rayTrace(Location arg0, Vector arg1, double arg2, FluidCollisionMode arg3) {
        throw NotImplementedException.createByLazy(Block.class,
                "rayTrace",
                Location.class,
                Vector.class,
                double.class,
                FluidCollisionMode.class);
    }

    @Override
    public double getTemperature() {
        throw NotImplementedException.createByLazy(Block.class, "getTemperature");
    }

    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        throw NotImplementedException.createByLazy(Block.class, "getPistonMoveReaction");
    }

    @Override
    public boolean isEmpty() {
        throw NotImplementedException.createByLazy(Block.class, "isEmpty");
    }

    @Override
    public Location getLocation(Location arg0) {
        throw NotImplementedException.createByLazy(Block.class, "getLocation", Location.class);
    }

    @Override
    public BlockState getState(boolean arg0) {
        throw NotImplementedException.createByLazy(Block.class, "getState", boolean.class);
    }

    //creating a tile entity from a snapshot is difficult. Some data specifically for tile entities is exposed by the snapshot, but not all
    @Override
    public @NotNull BlockState getState() {
        return AbstractBlockState.wrap(this.spongeLocation(), this.sponge().state(), true);
    }

    @Override
    public void setMetadata(@NotNull String metadataKey, @NotNull MetadataValue newMetadataValue) {
        throw NotImplementedException.createByLazy(Block.class, "setMetadata", String.class, MetadataValue.class);
    }

    @Override
    public @NotNull List<MetadataValue> getMetadata(@NotNull String metadataKey) {
        throw NotImplementedException.createByLazy(Block.class, "getMetadata", String.class);
    }

    @Override
    public boolean hasMetadata(@NotNull String metadataKey) {
        throw NotImplementedException.createByLazy(Block.class, "hasMetadata", String.class);
    }

    @Override
    public void removeMetadata(@NotNull String metadataKey, @NotNull Plugin owningPlugin) {
        throw NotImplementedException.createByLazy(Block.class, "removeMetadata", String.class, Plugin.class);
    }

}
