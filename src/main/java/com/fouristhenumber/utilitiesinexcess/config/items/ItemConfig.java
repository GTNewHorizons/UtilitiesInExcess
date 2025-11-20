package com.fouristhenumber.utilitiesinexcess.config.items;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.UnstableTools;
import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

@Config(modid = UtilitiesInExcess.MODID, category = "items")
public class ItemConfig {

    @Config.DefaultBoolean(false)
    public static boolean registerDisabledItems;

    @Config.DefaultBoolean(true)
    public static boolean shiftForDescription;

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(ItemConfig.class);
        UnstableTools.registerConfig();
        ConfigurationManager.registerConfig(WateringCanConfig.class);
        ConfigurationManager.registerConfig(InversionConfig.class);
        ConfigurationManager.registerConfig(FireBatteryConfig.class);
    }

    @Config.DefaultBoolean(true)
    public static boolean enableDiamondStick;

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

    @Config.DefaultBoolean(true)
    public static boolean enableBedrockium;

    @Config.DefaultBoolean(true)
    public static boolean enableGoldenBagOfHolding;

    @Config.DefaultBoolean(true)
    public static boolean enableXRayGlasses;

    @Config.DefaultBoolean(true)
    public static boolean enableBlockAnalyzer;
}
