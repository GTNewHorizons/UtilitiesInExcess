package com.fouristhenumber.utilitiesinexcess.config.dimensions;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.items.InversionConfig;
import com.fouristhenumber.utilitiesinexcess.config.items.invertedtools.InvertedTools;
import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

@Config.LangKey("utilitiesinexcess.config.dimensions")
@Config(modid = UtilitiesInExcess.MODID, category = "dimensions")
public class DimensionConfig {

    @Config.LangKey("utilitiesinexcess.config.dimension.underworld")
    public static final UnderWorldConfig underWorld = UnderWorldConfig.INSTANCE;

    @Config.LangKey("utilitiesinexcess.config.dimension.end_of_time")
    public static final EndOfTimeConfig endOfTime = EndOfTimeConfig.INSTANCE;

}
