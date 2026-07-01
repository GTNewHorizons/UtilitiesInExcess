package com.fouristhenumber.utilitiesinexcess.config;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

@Config.LangKey("utilitiesinexcess.config.other")
@Config(modid = UtilitiesInExcess.MODID, category = "other")
public class OtherConfig {

    @Config.Order(0)
    @Config.DefaultBoolean(true) // TODO Set default to false before release
    @Config.Comment("Enable the Extra Utilities to Utilities In Excess world conversion system")
    @Config.RequiresMcRestart
    public static boolean enableWorldConversion;

    @Config.Order(100)
    @Config.DefaultBoolean(true)
    @Config.Comment("Enable rendering some UIE baubles on players who have them equipped")
    @Config.RequiresMcRestart
    public static boolean enableBaubleRenders;

    @Config.Order(200)
    @Config.DefaultBoolean(true)
    @Config.Comment("Enables Tinkers' integration for bedrockium, inverted ingots, and magical wood")
    @Config.RequiresMcRestart
    public static boolean enableTinkersIntegration;

    @Config.Order(300)
    @Config.DefaultInt(314)
    @Config.Comment("314 is the tinkers material ID Extra Utilities uses for unstable and will therefore migrate tools automatically. Must change if both mods are installed!")
    @Config.RequiresMcRestart
    public static int invertedTinkersID;

    @Config.Order(400)
    @Config.DefaultInt(315)
    @Config.Comment("315 is the tinkers material ID Extra Utilities uses for bedrockium and will therefore migrate tools automatically. Must change if both mods are installed!")
    @Config.RequiresMcRestart
    public static int bedrockiumTinkersID;

    @Config.Order(500)
    @Config.DefaultInt(316)
    @Config.Comment("316 is the tinkers material ID Extra Utilities uses for magical wood and will therefore migrate tools automatically. Must change if both mods are installed!")
    @Config.RequiresMcRestart
    public static int magicalWoodTinkersID;
}
