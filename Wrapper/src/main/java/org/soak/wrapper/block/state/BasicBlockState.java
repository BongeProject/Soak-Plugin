package org.soak.wrapper.block.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.server.ServerLocation;

public class BasicBlockState extends AbstractBlockState {

    public BasicBlockState(@NotNull ServerLocation location) {
        super(location);
    }

    public BasicBlockState(@Nullable ServerLocation location, @NotNull BlockState state) {
        super(location, state);
    }

    @Override
    protected AbstractBlockState createCopy(@Nullable ServerLocation location, @NotNull BlockState state) {
        return new BasicBlockState(location, state);
    }

    @Override
    protected void onPostApply(@NotNull ServerLocation location) {

    }
}
