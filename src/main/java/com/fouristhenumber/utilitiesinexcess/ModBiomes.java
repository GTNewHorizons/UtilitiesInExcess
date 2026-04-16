package com.fouristhenumber.utilitiesinexcess;

import java.util.function.Function;

import net.minecraft.world.biome.BiomeGenBase;

import com.fouristhenumber.utilitiesinexcess.common.dimensions.endoftime.BiomeGenEndOfTime;
import com.fouristhenumber.utilitiesinexcess.common.dimensions.underworld.BiomeGenUnderWorld;
import com.fouristhenumber.utilitiesinexcess.config.dimensions.EndOfTimeConfig;
import com.fouristhenumber.utilitiesinexcess.config.dimensions.UnderWorldConfig;

/// Adapted from [ModBlocks]
public enum ModBiomes {
    // spotless:off

    // make sure to leave a trailing comma
    UNDERWORLD(UnderWorldConfig.enableUnderWorldBiome, UnderWorldConfig.underWorldBiomeId, BiomeGenUnderWorld::new),
    END_OF_TIME(EndOfTimeConfig.enableEndOfTimeBiome, EndOfTimeConfig.endOfTimeBiomeId, BiomeGenEndOfTime::new),
    ; // leave trailing semicolon
    // spotless:on

    public static void init() {

    }

    private final boolean isEnabled;
    private final int biomeId;
    private final BiomeGenBase biome;

    ModBiomes(boolean isEnabled, int id, Function<Integer, ? extends BiomeGenBase> ctor) {
        this.isEnabled = isEnabled;
        this.biomeId = id;

        this.biome = isEnabled ? ctor.apply(id) : null;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public BiomeGenBase getBiome() {
        if (!isEnabled) throw new IllegalStateException("Biome " + name() + " is disabled");

        return biome;
    }

    public int getBiomeId() {
        return biomeId;
    }
}
