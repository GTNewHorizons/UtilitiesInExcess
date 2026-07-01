package com.fouristhenumber.utilitiesinexcess.config.items;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.items.invertedtools.InvertedTools;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config.LangKey("utilitiesinexcess.config.items")
@Config(modid = UtilitiesInExcess.MODID, category = "items")
@Config.Order(100)
public class ItemConfig {

    @Config.DefaultBoolean(true)
    public static boolean enableHeavenlyRing;

    @Config.DefaultBoolean(true)
    public static boolean enableMobJar;

    @Config.DefaultBoolean(true)
    public static boolean enableBedrockium;

    @Config.DefaultBoolean(true)
    public static boolean enableGoldenBagOfHolding;

    @Config.DefaultBoolean(true)
    public static boolean enableXRayGlasses;

    @Config.DefaultBoolean(true)
    public static boolean enableBlockAnalyzer;

    @Config.DefaultBoolean(true)
    public static boolean enableGlove;

    @Config.LangKey("utilitiesinexcess.config.item.inverted_tools")
    public static final InvertedTools invertedTools = InvertedTools.INSTANCE;

    @Config.LangKey("utilitiesinexcess.config.item.inversion")
    public static final InversionConfig inversion = InversionConfig.INSTANCE;

    @Config.LangKey("utilitiesinexcess.config.item.watering_can")
    public static final WateringCanConfig wateringCan = WateringCanConfig.INSTANCE;

    @Config.LangKey("item.chunchunmaru.name")
    public static final ChunchunmaruConfig chunchunmaru = ChunchunmaruConfig.INSTANCE;

    @Config.LangKey("item.fire_battery.name")
    public static final FireBatteryConfig fireBattery = FireBatteryConfig.INSTANCE;

    @Config.LangKey("utilitiesinexcess.config.item.architects_wands")
    public static final ArchitectsWandsConfig architectsWands = ArchitectsWandsConfig.INSTANCE;
}
