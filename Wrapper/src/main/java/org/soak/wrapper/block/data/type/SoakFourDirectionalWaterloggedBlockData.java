package org.soak.wrapper.block.data.type;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.soak.wrapper.block.data.AbstractBlockData;
import org.soak.wrapper.block.data.SoakFourDirectional;
import org.soak.wrapper.block.data.SoakWaterLogged;
import org.spongepowered.api.block.BlockState;

public class SoakFourDirectionalWaterloggedBlockData extends AbstractBlockData implements SoakFourDirectional, SoakWaterLogged {

    public SoakFourDirectionalWaterloggedBlockData(BlockState state) {
        super(state);
    }

    @Override
    public @NotNull AbstractBlockData clone() {
        return new SoakFourDirectionalWaterloggedBlockData(this.spongeState);
    }
}
