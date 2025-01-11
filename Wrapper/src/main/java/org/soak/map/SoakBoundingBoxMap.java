package org.soak.map;

import org.bukkit.util.BoundingBox;
import org.spongepowered.api.util.AABB;

public class SoakBoundingBoxMap {

    public static AABB toSponge(BoundingBox box) {
        var min = box.getMin();
        var max = box.getMax();
        return AABB.of(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }
}
