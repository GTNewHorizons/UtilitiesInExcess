package com.fouristhenumber.utilitiesinexcess.config.dimensions;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

@Config(modid = UtilitiesInExcess.MODID, category = "dimensions")
public class DimensionConfig {

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(DimensionConfig.class);
        ConfigurationManager.registerConfig(UnderWorldConfig.class);
        ConfigurationManager.registerConfig(EndOfTimeConfig.class);
    }

}
