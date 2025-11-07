package com.fouristhenumber.utilitiesinexcess.config.dimensions;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "dimensions.under_world")
public class UnderWorldConfig {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableUnderWorld;

    @Config.DefaultInt(10)
    @Config.RequiresMcRestart
    public static int underWorldDimensionId;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Comment("Enables the underworld biome")
    public static boolean enableUnderWorldBiome;

    @Config.DefaultInt(50)
    @Config.RequiresMcRestart
    @Config.Comment("The biome ID for the underworld biome")
    public static int underWorldBiomeId;

    @Config.DefaultInt(50)
    @Config.RequiresMcRestart
    @Config.Comment("The biome to populate the underworld with")
    public static int defaultBiomeId;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Comment("Enables vanilla ore spawning")
    public static boolean spawnVanillaOre;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Comment("Enables custom ore spawning (typically used by mods to generate their ore)")
    public static boolean spawnCustomOre;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Comment("Enables increased ore rates and aggressive mob spawning in certain regions")
    public static boolean enableDifficulty;

}
