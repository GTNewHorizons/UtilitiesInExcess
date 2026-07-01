package com.fouristhenumber.utilitiesinexcess.config.items.invertedtools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.inverted_tools.precision_shears")
public class PrecisionShearsConfig {

    @Config.Ignore
    public static final PrecisionShearsConfig INSTANCE = new PrecisionShearsConfig();

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public boolean enable;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public boolean unbreakable;

    @Config.DefaultBoolean(true)
    public boolean spawnParticles;
    @Config.DefaultInt(1)
    @Config.RangeInt(min = 0)
    @Config.RequiresMcRestart
    public int toolLevel;

    @Config.DefaultFloat(4)
    @Config.RangeFloat(min = 0, max = 100)
    @Config.Sync
    public float efficiency;

    @Config.DefaultInt(20)
    @Config.RangeInt(min = 0)
    @Config.Sync
    public int cooldown;
}
