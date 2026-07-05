package com.fouristhenumber.utilitiesinexcess.config.dimensions;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "dimensions.under_world")
public class UnderWorldConfig {

    @Config.Ignore
    public static final UnderWorldConfig INSTANCE = new UnderWorldConfig();

    @Config.Order(0)
    @Config.DefaultBoolean(true)
    @Config.Name("Enable")
    @Config.RequiresMcRestart
    public boolean enableUnderWorld;

    @Config.Order(100)
    @Config.DefaultInt(10)
    @Config.RequiresMcRestart
    public int underWorldDimensionId;

    @Config.Order(200)
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Comment("Enables the underworld biome")
    public boolean enableUnderWorldBiome;

    @Config.Order(300)
    @Config.DefaultInt(50)
    @Config.RequiresMcRestart
    @Config.Comment("The biome ID for the underworld biome")
    public int underWorldBiomeId;

    @Config.Order(400)
    @Config.DefaultInt(50)
    @Config.RequiresMcRestart
    @Config.Comment("The biome to populate the underworld with")
    public int defaultBiomeId;

    @Config.Order(500)
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Comment("Enables vanilla ore spawning")
    public boolean spawnVanillaOre;

    @Config.Order(600)
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Comment("Enables custom ore spawning (typically used by mods to generate their ore)")
    public boolean spawnCustomOre;

    @Config.Order(700)
    @Config.DefaultBoolean(true)
    @Config.Comment("Enables increased ore rates and aggressive mob spawning in certain regions")
    public boolean enableDifficulty;

}
