package org.soak.wrapper.block.state;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.soak.exception.NotImplementedException;
import org.soak.map.SoakColourMap;
import org.soak.map.SoakMessageMap;
import org.soak.map.SoakVectorMap;
import org.soak.utils.NullUtils;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SignBlockState extends AbstractTileState implements Sign {

    private final SoakSignSide front = new SoakSignSide();
    private final SoakSignSide back = new SoakSignSide();

    public SignBlockState(@Nullable ServerLocation location, @NotNull BlockState state, boolean isSnapshot) {
        super(location, state, isSnapshot);
    }

    @Override
    @Deprecated
    public @NotNull List<Component> lines() {
        return front.lines();
    }

    @Override
    @Deprecated
    public @NotNull Component line(int i) throws IndexOutOfBoundsException {
        return front.line(i);
    }

    @Override
    @Deprecated
    public void line(int i, @NotNull Component component) throws IndexOutOfBoundsException {
        this.front.line(i, component);
    }

    @Override
    @Deprecated
    public @NotNull String[] getLines() {
        return this.front.getLines();
    }

    @Override
    @Deprecated
    public @NotNull String getLine(int i) throws IndexOutOfBoundsException {
        return this.front.getLine(i);
    }

    @Override
    @Deprecated
    public void setLine(int i, @NotNull String s) throws IndexOutOfBoundsException {
        this.front.setLine(i, s);
    }

    @Override
    @Deprecated
    public boolean isEditable() {
        return this.isWaxed();
    }

    @Override
    @Deprecated
    public void setEditable(boolean b) {
        this.setWaxed(b);
    }

    @Override
    public boolean isWaxed() {
        return key(Keys.SIGN_WAXED).getValue();
    }

    @Override
    public void setWaxed(boolean b) {
        key(Keys.SIGN_WAXED).setValue(b);
    }

    @Override
    public boolean isGlowingText() {
        return this.front.isGlowingText();
    }

    @Override
    public void setGlowingText(boolean b) {
        this.front.setGlowingText(b);
    }

    @Override
    public @NotNull DyeColor getColor() {
        return this.front.getColor();
    }

    @Override
    public void setColor(@NotNull DyeColor dyeColor) {
        this.front.setColor(dyeColor);
    }

    @Override
    public @NotNull SignSide getSide(@NotNull Side side) {
        return switch (side) {
            case FRONT -> this.front;
            case BACK -> this.back;
        };
    }

    @Override
    public @NotNull SignSide getTargetSide(@NotNull Player player) {
        var direction = this.state().get(Keys.DIRECTION).orElseThrow();
        Vector3d blockPosition = NullUtils.mapTo(this.location(), Location::position, () -> Vector3d.from(0, 0, 0));
        Vector3d playerPosition = SoakVectorMap.to3d(player.getLocation().toVector());

        var difference = blockPosition.min(playerPosition);
        return switch (direction) {
            case UP, DOWN, NONE -> throw new RuntimeException("Sign is facing up or down or none?");
            case NORTH, NORTH_NORTHEAST, NORTH_NORTHWEST, NORTHWEST, NORTHEAST -> difference.x() > 0 ? back : front;
            case SOUTH, SOUTH_SOUTHEAST, SOUTH_SOUTHWEST, SOUTHEAST, SOUTHWEST -> difference.x() > 0 ? front : back;
            case EAST_NORTHEAST, EAST_SOUTHEAST, EAST -> difference.z() > 0 ? back : front;
            case WEST_NORTHWEST, WEST_SOUTHWEST, WEST -> difference.z() > 0 ? front : back;
            default -> throw new RuntimeException("Unknown direction of " + direction.name());
        };
    }

    @Override
    public @Nullable Player getAllowedEditor() {
        var id = getAllowedEditorUniqueId();
        if (id == null) {
            return null;
        }
        return Bukkit.getServer().getPlayer(id);
    }

    @Override
    public @Nullable UUID getAllowedEditorUniqueId() {
        throw NotImplementedException.createByLazy(BlockState.class, "getAllowedEditorUniqueId");
    }

    @Override
    public void setAllowedEditorUniqueId(@Nullable UUID uuid) {
        throw NotImplementedException.createByLazy(BlockState.class, "setAllowedEditorUniqueId", UUID.class);

    }

    @Override
    public @NotNull Side getInteractableSideFor(double v, double v1) {
        throw NotImplementedException.createByLazy(BlockState.class, "getInteractableSideFor", double.class, double.class);

    }

    @Override
    protected SignBlockState createCopy(@Nullable ServerLocation location, @NotNull BlockState state) {
        var ret = new SignBlockState(location, state, true);
        ret.back.lines.addAll(this.back.lines);
        ret.front.lines.addAll(this.front.lines);
        ret.back.colour = this.back.colour;
        ret.front.colour = this.front.colour;
        return ret;
    }

    @Override
    protected void onPostApply(@NotNull ServerLocation location) {
        var sign = (org.spongepowered.api.block.entity.Sign) location.blockEntity().orElseThrow(() -> new RuntimeException("Sign BlockEntity was not set"));
        apply(sign.backText(), this.back);
        apply(sign.frontText(), this.front);
    }

    private void apply(org.spongepowered.api.block.entity.Sign.SignText text, SoakSignSide side) {
        text.offer(Keys.GLOWING_TEXT, side.isGlowingText());
        text.offer(Keys.SIGN_LINES, side.lines());
        text.offer(Keys.COLOR, SoakColourMap.toSponge(side.getColor().getColor()));
    }

    public class SoakSignSide implements SignSide {

        private final List<Component> lines = new ArrayList<>();
        private DyeColor colour;

        @Override
        public @NotNull List<Component> lines() {
            return this.lines;
        }

        @Override
        public @NotNull Component line(int i) throws IndexOutOfBoundsException {
            return this.lines.get(i);
        }

        @Override
        public void line(int i, @NotNull Component component) throws IndexOutOfBoundsException {
            this.lines.set(i, component);
        }

        @Override
        public @NotNull String[] getLines() {
            return this.lines.stream().map(SoakMessageMap::mapToBukkit).toArray(String[]::new);
        }

        @Override
        public @NotNull String getLine(int i) throws IndexOutOfBoundsException {
            return SoakMessageMap.mapToBukkit(this.line(i));
        }

        @Override
        public void setLine(int i, @NotNull String s) throws IndexOutOfBoundsException {
            line(i, SoakMessageMap.toComponent(s));
        }

        @Override
        public boolean isGlowingText() {
            return key(Keys.GLOWING_TEXT).getValue();
        }

        @Override
        public void setGlowingText(boolean b) {
            key(Keys.GLOWING_TEXT).setValue(b);
        }

        @Override
        public @Nullable DyeColor getColor() {
            return this.colour;
        }

        @Override
        public void setColor(DyeColor dyeColor) {
            this.colour = dyeColor;
        }
    }
}
