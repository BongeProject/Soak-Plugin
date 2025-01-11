package org.soak.wrapper.block.state;

import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.server.ServerLocation;

public abstract class AbstractTileState extends AbstractBlockState implements TileState {

    private final boolean isSnapshot;

    public AbstractTileState(@NotNull ServerLocation location, boolean isSnapshot) {
        super(location);
        this.isSnapshot = isSnapshot;
    }

    public AbstractTileState(@Nullable ServerLocation location, @NotNull BlockState state, boolean isSnapshot) {
        super(location, state);
        this.isSnapshot = isSnapshot;
    }

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        throw NotImplementedException.createByLazy(TileState.class, "getPersistentDataContainer");
    }

    @Override
    public boolean isSnapshot() {
        return this.isSnapshot;
    }
}
