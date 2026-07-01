package com.fouristhenumber.utilitiesinexcess.config.items;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.items.invertedtools.InvertedTools;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config.LangKey("utilitiesinexcess.config.item")
@Config(modid = UtilitiesInExcess.MODID, category = "items")
public class ItemConfig {

    @Config.DefaultBoolean(true)
    public static boolean enableHeavenlyRing;

    @Config.DefaultBoolean(true)
    public static boolean enableMobJar;

    @Config.DefaultBoolean(true)
    public static boolean enableArchitectsWand;

    @Config.DefaultInt(9)
    public static int architectsWandBuildLimit;

    @Config.DefaultBoolean(true)
    public static boolean enableSuperArchitectsWand;

    @Config.DefaultInt(49)
    public static int superArchitectsWandBuildLimit;

    @Config.DefaultInt(200)
    public static int architectsWandCreativeBuildLimit;

    @Config.DefaultInt(100)
    @Config.Comment("[GT5U] Durability damage dealt to Trowels per block placed by the Architect's Wand. (Set to 0 to disable)")
    public static int damageTrowelWithArchitectsWand;

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
    public static final InvertedTools INVERTED_TOOLS = InvertedTools.INSTANCE;

    @Config.LangKey("utilitiesinexcess.config.item.inversion")
    public static final InversionConfig inversion = InversionConfig.INSTANCE;

    @Config.LangKey("utilitiesinexcess.config.item.watering_can")
    public static final WateringCanConfig wateringCan = WateringCanConfig.INSTANCE;

    @Config.LangKey("item.chunchunmaru.name")
    public static final ChunchunmaruConfig chunchunmaru = ChunchunmaruConfig.INSTANCE;

    @Config.LangKey("item.fire_battery.name")
    public static final FireBatteryConfig fireBattery = FireBatteryConfig.INSTANCE;
}
