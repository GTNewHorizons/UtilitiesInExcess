package com.fouristhenumber.utilitiesinexcess.config.items;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.fire_battery")
public class FireBatteryConfig {

    @Config.Ignore
    public static final FireBatteryConfig INSTANCE = new FireBatteryConfig();

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public boolean enableFireBattery;

    @Config.Comment("Maximum RF storage value.")
    @Config.DefaultInt(1500000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public int fireBatteryRFStorage;

    @Config.Comment("Maximum charge rate.")
    @Config.DefaultInt(1500000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public int fireBatteryRFCharge;

    @Config.Comment("Maximum RF consumption.")
    @Config.DefaultInt(15000)
    @Config.RangeInt(min = 0)
    @Config.RequiresMcRestart
    public int fireBatteryRFUsage;

    @Config.Comment("RF consumed per burn tick.")
    @Config.DefaultInt(50)
    @Config.RangeInt(min = 1)
    public int fireBatteryBurnTime;
}
