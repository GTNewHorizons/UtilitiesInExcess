package com.fouristhenumber.utilitiesinexcess.config;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

@Config(modid = UtilitiesInExcess.MODID, category = "other")
public class OtherConfig {

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(OtherConfig.class);
    }

    @Config.DefaultBoolean(true) // TODO Set default to false before release
    @Config.RequiresMcRestart
    @Config.Comment("Enable the Extra Utilities to Utilities In Excess world conversion system")
    public static boolean enableWorldConversion;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Comment("Enable rendering some UIE baubles on players who have them equipped")
    public static boolean enableBaubleRenders;
}
