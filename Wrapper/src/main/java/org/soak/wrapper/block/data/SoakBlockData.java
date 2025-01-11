package org.soak.wrapper.block.data;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.soak.exception.NotImplementedException;
import org.spongepowered.api.block.BlockState;

public class SoakBlockData extends AbstractBlockData {
    public SoakBlockData(BlockState state) {
        super(state);
    }

    @Override
    public @NotNull AbstractBlockData clone() {
        return new SoakBlockData(this.sponge());
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull Location location) {
        throw NotImplementedException.createByLazy(BlockData.class, "getCollisionShape", Location.class);
    }

    @Override
    public @NotNull Color getMapColor() {
        throw NotImplementedException.createByLazy(BlockData.class, "getMapColor");
    }

    @Override
    public void copyTo(@NotNull BlockData blockData) {

    }

    @Override
    public org.bukkit.block.@NotNull BlockState createBlockState() {
        return null;
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack itemStack, boolean b) {
        return 0;
    }
}
