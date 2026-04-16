package com.fouristhenumber.utilitiesinexcess.config.items.unstabletools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.unstable_tools.precision_shears")
public class PrecisionShearsConfig {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enable;
    @Config.DefaultBoolean(true)
    public static boolean unbreakable;
    @Config.DefaultBoolean(true)
    public static boolean spawnParticles;
    @Config.DefaultInt(1)
    @Config.RangeInt(min = 0)
    public static int toolLevel;
    @Config.DefaultFloat(4)
    @Config.RangeFloat(min = 0, max = 100)
    public static float efficiency;
    @Config.DefaultInt(20)
    @Config.RangeInt(min = 0)
    public static int cooldown;
}
