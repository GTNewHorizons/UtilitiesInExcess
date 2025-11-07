package com.fouristhenumber.utilitiesinexcess;

import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

import com.fouristhenumber.utilitiesinexcess.common.dimensions.underworld.WorldProviderUnderWorld;
import com.fouristhenumber.utilitiesinexcess.config.dimensions.UnderWorldConfig;

/// Adapted from [ModBlocks]
public enum ModDimensions {
    // spotless:off

    // make sure to leave a trailing comma
    UNDERWORLD(UnderWorldConfig.enableUnderWorld, UnderWorldConfig.underWorldDimensionId, WorldProviderUnderWorld.class),
    ; // leave trailing semicolon
    // spotless:on

    public static final ModDimensions[] VALUES = values();

    public static void init() {
        for (ModDimensions dimension : VALUES) {
            if (dimension.isEnabled()) {
                DimensionManager.registerProviderType(dimension.providerId, dimension.provider, dimension.keepLoaded);
                DimensionManager.registerDimension(dimension.dimensionId, dimension.providerId);
            }
        }
    }

    private final boolean isEnabled;
    private final int providerId, dimensionId;
    /**
     * null == default ItemBlock
     */
    private final Class<? extends WorldProvider> provider;
    private final boolean keepLoaded;

    ModDimensions(boolean isEnabled, int id, Class<? extends WorldProvider> provider) {
        this(isEnabled, id, id, provider, false);
    }

    ModDimensions(boolean isEnabled, int providerId, int dimensionId, Class<? extends WorldProvider> provider,
        boolean keepLoaded) {
        this.isEnabled = isEnabled;
        this.providerId = providerId;
        this.dimensionId = dimensionId;
        this.provider = provider;
        this.keepLoaded = keepLoaded;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

}
