package com.fouristhenumber.utilitiesinexcess.config.dimensions;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "dimensions.end_of_time")
public class EndOfTimeConfig {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableEndOfTime;

    @Config.DefaultInt(11)
    @Config.RequiresMcRestart
    public static int endOfTimeDimensionId;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Comment("Enables the End of Time biome")
    public static boolean enableEndOfTimeBiome;

    @Config.DefaultInt(51)
    @Config.RequiresMcRestart
    @Config.Comment("The biome ID for the End of Time biome")
    public static int endOfTimeBiomeId;

    @Config.DefaultInt(51)
    @Config.RequiresMcRestart
    @Config.Comment("The biome to populate the End of Time with")
    public static int defaultBiomeId;

    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    @Config.Comment("Enables rain in the End of Time")
    public static boolean endOfTimeRain;

    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    @Config.Comment("Enables natural mob spawning in the End of Time")
    public static boolean endOfTimeSpawning;

}
