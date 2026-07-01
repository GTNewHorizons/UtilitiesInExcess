package com.fouristhenumber.utilitiesinexcess.config.items;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.items.invertedtools.InvertedTools;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config.LangKey("utilitiesinexcess.config.items")
@Config(modid = UtilitiesInExcess.MODID, category = "items")
@Config.Order(100)
public class ItemConfig {

    @Config.Order(0)
    @Config.LangKey("utilitiesinexcess.config.item.inverted_tools")
    public static final InvertedTools invertedTools = InvertedTools.INSTANCE;

    @Config.Order(100)
    @Config.LangKey("utilitiesinexcess.config.item.inversion")
    public static final InversionConfig inversion = InversionConfig.INSTANCE;

    @Config.Order(200)
    @Config.LangKey("utilitiesinexcess.config.item.architects_wands")
    public static final ArchitectsWandsConfig architectsWands = ArchitectsWandsConfig.INSTANCE;

    @Config.Order(300)
    @Config.LangKey("utilitiesinexcess.config.item.watering_can")
    public static final WateringCanConfig wateringCan = WateringCanConfig.INSTANCE;

    @Config.Order(400)
    @Config.LangKey("item.chunchunmaru.name")
    public static final ChunchunmaruConfig chunchunmaru = ChunchunmaruConfig.INSTANCE;

    @Config.Order(500)
    @Config.LangKey("item.fire_battery.name")
    public static final FireBatteryConfig fireBattery = FireBatteryConfig.INSTANCE;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableHeavenlyRing;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMobJar;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableBedrockium;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableGoldenBagOfHolding;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableXRayGlasses;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableBlockAnalyzer;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableGlove;
}
