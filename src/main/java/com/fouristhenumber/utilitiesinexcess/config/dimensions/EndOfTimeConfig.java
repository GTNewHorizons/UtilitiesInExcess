package com.fouristhenumber.utilitiesinexcess.config.dimensions;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "dimensions.end_of_time")
public class EndOfTimeConfig {

    @Config.Ignore
    public static final EndOfTimeConfig INSTANCE = new EndOfTimeConfig();

    @Config.Order(0)
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Name("Enable")
    public boolean enableEndOfTime;

    @Config.Order(100)
    @Config.DefaultInt(11)
    @Config.RequiresMcRestart
    public int endOfTimeDimensionId;

    @Config.Order(200)
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Comment("Enables the End of Time biome")
    public boolean enableEndOfTimeBiome;

    @Config.Order(300)
    @Config.DefaultInt(51)
    @Config.RequiresMcRestart
    @Config.Comment("The biome ID for the End of Time biome")
    public int endOfTimeBiomeId;

    @Config.Order(400)
    @Config.DefaultInt(51)
    @Config.RequiresMcRestart
    @Config.Comment("The biome to populate the End of Time with")
    public int defaultBiomeId;

    @Config.Order(500)
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    @Config.Comment("Enables rain in the End of Time")
    public boolean endOfTimeRain;

    @Config.Order(600)
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    @Config.Comment("Enables natural mob spawning in the End of Time")
    public boolean endOfTimeSpawning;

}
