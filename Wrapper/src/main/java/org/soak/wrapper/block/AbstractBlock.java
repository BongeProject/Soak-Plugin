package org.soak.wrapper.block;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.soak.WrapperManager;
import org.soak.map.SoakBlockMap;
import org.soak.map.SoakDirectionMap;
import org.soak.map.item.SoakItemStackMap;
import org.soak.plugin.SoakManager;
import org.soak.wrapper.block.data.AbstractBlockData;
import org.soak.wrapper.world.SoakWorld;
import org.soak.wrapper.world.chunk.AbstractSoakChunk;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.MatterTypes;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3i;

import java.util.Set;

public abstract class AbstractBlock<Holder extends DataHolder> implements Block {

    protected Holder block;

    public AbstractBlock(Holder holder) {
        this.block = holder;
    }

    public abstract org.spongepowered.api.block.BlockState spongeBlockState();

    public Holder sponge() {
        return this.block;
    }

    public abstract Vector3i spongePosition();

    public abstract @NotNull SoakWorld getWorld();

    public ServerLocation spongeLocation() {
        var world = this.getWorld().sponge();
        return world.location(spongePosition());
    }

    @Override
    public @NotNull String translationKey() {
        return this.getTranslationKey();
    }

    @Override
    public @NotNull Material getType() {
        return SoakBlockMap.toBukkit(this.spongeBlockState().type());
    }

    @Override
    public void setType(Material arg0) {
        this.setType(arg0, true);
    }

    @Override
    public @NotNull Block getRelative(int arg0, int arg1, int arg2) {
        return new SoakBlock(spongeLocation().add(arg0, arg1, arg2));
    }

    @Override
    public @NotNull Block getRelative(@NotNull BlockFace arg0, int arg1) {
        var direction = SoakDirectionMap.toSponge(arg0).asBlockOffset().mul(arg1);
        return new SoakBlock(this.spongeLocation().add(direction));
    }

    @Override
    public @NotNull Block getRelative(BlockFace arg0) {
        return this.getRelative(arg0.getModX(), arg0.getModY(), arg0.getModZ());
    }

    @Override
    public @NotNull Location getLocation() {
        var loc = new Location(this.getWorld(), this.getX(), this.getY(), this.getZ());
        return ((AbstractBlockData) getBlockData()).withDefaultY(loc);
    }

    @Override
    public byte getLightLevel() {
        return this.block.get(Keys.LIGHT_EMISSION).orElse(0).byteValue();
    }

    @Override
    public byte getLightFromSky() {
        return this.block.get(Keys.SKY_LIGHT).orElse(0).byteValue();
    }

    @Override
    public byte getLightFromBlocks() {
        return this.block.get(Keys.BLOCK_LIGHT).orElse(0).byteValue();
    }

    @Override
    public @NotNull BlockData getBlockData() {
        return SoakManager.<WrapperManager>getManager().getMemoryStore().get(this.spongeBlockState());
    }

    @Override
    public int getX() {
        return this.spongePosition().x();
    }

    @Override
    public int getY() {
        return this.spongePosition().y();
    }

    @Override
    public int getZ() {
        return this.spongePosition().z();
    }

    @Override
    public Chunk getChunk() {
        var spongeLoc = this.spongeLocation();
        var chunkLoc = spongeLoc.chunkPosition();
        var spongeChunk = spongeLoc.world().chunk(chunkLoc);
        return new AbstractSoakChunk(spongeChunk);
    }

    @Override
    public boolean isBlockPowered() {
        return this.spongeBlockState().get(Keys.IS_POWERED).orElse(false);
    }

    @Override
    public boolean isBlockIndirectlyPowered() {
        return this.spongeBlockState().get(Keys.IS_INDIRECTLY_POWERED).orElse(false);
    }

    @Override
    public int getBlockPower() {
        return this.spongeBlockState().get(Keys.POWER).orElse(0);
    }

    @Override
    public boolean isLiquid() {
        return this.spongeBlockState().get(Keys.MATTER_TYPE).map(type -> type.equals(MatterTypes.LIQUID.get())).orElse(false);
    }

    @Override
    public boolean isSolid() {
        return this.spongeBlockState().get(Keys.MATTER_TYPE).map(type -> type.equals(MatterTypes.SOLID.get())).orElse(false);
    }

    @Override
    public boolean isPreferredTool(ItemStack arg0) {
        var spongeItem = SoakItemStackMap.toSponge(arg0);
        var thisBlockType = this.spongeBlockState().type();
        return spongeItem.getOrElse(Keys.BREAKABLE_BLOCK_TYPES, Set.of()).stream().anyMatch(blockType -> blockType.equals(thisBlockType));
    }

}
