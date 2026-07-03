package com.fouristhenumber.utilitiesinexcess.config.dimensions;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config.LangKey("utilitiesinexcess.config.dimensions")
@Config(modid = UtilitiesInExcess.MODID, category = "dimensions")
@Config.Order(300)
public class DimensionConfig {

    @Config.LangKey("utilitiesinexcess.config.dimension.underworld")
    public static final UnderWorldConfig underWorld = UnderWorldConfig.INSTANCE;

    @Config.LangKey("utilitiesinexcess.config.dimension.end_of_time")
    public static final EndOfTimeConfig endOfTime = EndOfTimeConfig.INSTANCE;

}
