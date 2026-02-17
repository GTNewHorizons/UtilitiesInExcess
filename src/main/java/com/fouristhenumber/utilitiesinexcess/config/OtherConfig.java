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

    @Config.DefaultBoolean(true)
    @Config.Comment("Enables Tinkers' integration for bedrockium, inverted ingots, and magical wood")
    public static boolean enableTinkersIntegration;

    @Config.DefaultInt(314)
    @Config.Comment("314 is the tinkers material ID Extra Utilities uses for unstable and will therefore migrate tools automatically. Must change if both mods are installed!")
    public static int invertedTinkersID;

    @Config.DefaultInt(315)
    @Config.Comment("315 is the tinkers material ID Extra Utilities uses for bedrockium and will therefore migrate tools automatically. Must change if both mods are installed!")
    public static int bedrockiumTinkersID;

    @Config.DefaultInt(316)
    @Config.Comment("316 is the tinkers material ID Extra Utilities uses for magical wood and will therefore migrate tools automatically. Must change if both mods are installed!")
    public static int magicalWoodTinkersID;
}
