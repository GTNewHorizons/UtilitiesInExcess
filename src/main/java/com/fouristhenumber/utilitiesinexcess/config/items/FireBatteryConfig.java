package com.fouristhenumber.utilitiesinexcess.config.items;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.fire_battery")
public class FireBatteryConfig {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableFireBattery;

    @Config.Comment("Maximum RF storage value.")
    @Config.DefaultInt(1500000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int fireBatteryRFStorage;

    @Config.Comment("Maximum charge rate.")
    @Config.DefaultInt(1500000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int fireBatteryRFCharge;

    @Config.Comment("Maximum RF consumption.")
    @Config.DefaultInt(15000)
    @Config.RangeInt(min = 0)
    public static int fireBatteryRFUsage;

    @Config.Comment("RF consumed per burn tick.")
    @Config.DefaultInt(50)
    @Config.RangeInt(min = 1)
    public static int fireBatteryBurnTime;
}
