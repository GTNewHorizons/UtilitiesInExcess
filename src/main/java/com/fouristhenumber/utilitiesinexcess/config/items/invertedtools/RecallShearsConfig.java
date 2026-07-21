package com.fouristhenumber.utilitiesinexcess.config.items.invertedtools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.inverted_tools.recall_shears")
public class RecallShearsConfig {

    @Config.Ignore
    public static final RecallShearsConfig INSTANCE = new RecallShearsConfig();

    @Config.Order(0)
    @Config.DefaultBoolean(true)
    @Config.Name("Enable")
    @Config.RequiresMcRestart
    public boolean enable;

    @Config.Order(100)
    @Config.DefaultBoolean(true)
    @Config.Name("Unbreakable")
    @Config.RequiresMcRestart
    public boolean unbreakable;

    @Config.Order(200)
    @Config.DefaultBoolean(true)
    public boolean spawnParticles;

    @Config.Order(300)
    @Config.DefaultInt(1)
    @Config.RangeInt(min = 0)
    @Config.RequiresMcRestart
    public int toolLevel;

    @Config.Order(400)
    @Config.DefaultFloat(4)
    @Config.RangeFloat(min = 0, max = 100)
    @Config.Sync
    public float efficiency;

    @Config.Order(500)
    @Config.DefaultInt(20)
    @Config.RangeInt(min = 0)
    @Config.Sync
    public int cooldown;
}
