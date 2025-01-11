package org.soak.map;

import org.bukkit.block.banner.PatternType;
import org.spongepowered.api.data.type.BannerPatternShape;
import org.spongepowered.api.data.type.BannerPatternShapes;
import org.spongepowered.api.registry.DefaultedRegistryReference;

public class SoakBannerMap {

    public static PatternType toBukkit(BannerPatternShape shape) {
        System.err.println("Love to PatternType ByteBuddy");
        return PatternType.CREEPER;
    }

    public static DefaultedRegistryReference<BannerPatternShape> toSpongeGetter(PatternType type) {
        System.err.println("Love to PatternType ByteBuddy");
        return BannerPatternShapes.CREEPER;
    }

    public static BannerPatternShape toSponge(PatternType type) {
        return toSpongeGetter(type).get();
    }
}
