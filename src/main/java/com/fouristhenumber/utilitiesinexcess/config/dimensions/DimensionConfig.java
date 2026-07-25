package com.fouristhenumber.utilitiesinexcess.config.dimensions;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config.LangKey("uie.config.dimensions")
@Config(modid = UtilitiesInExcess.MODID, category = "dimensions")
@Config.Order(300)
public class DimensionConfig {

    @Config.LangKey("uie.config.dimension.underworld")
    public static final UnderWorldConfig underWorld = UnderWorldConfig.INSTANCE;

    @Config.LangKey("uie.config.dimension.end_of_time")
    public static final EndOfTimeConfig endOfTime = EndOfTimeConfig.INSTANCE;

}
