package com.fouristhenumber.utilitiesinexcess.config;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items")
public class ItemConfig {

    @Config.DefaultBoolean(true)
    public static boolean enableHungerAxe;

    @Config.DefaultBoolean(true)
    public static boolean enableHeavenlyRing;

    @Config.DefaultBoolean(true)
    public static boolean enableMobJar;

    @Config.DefaultBoolean(true)
    public static boolean enableArchitectsWand;
}
