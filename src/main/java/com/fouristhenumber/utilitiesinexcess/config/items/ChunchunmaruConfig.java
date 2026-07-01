package com.fouristhenumber.utilitiesinexcess.config.items;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.Chunchunmaru")
public class ChunchunmaruConfig {

    @Config.Ignore
    public static final ChunchunmaruConfig INSTANCE = new ChunchunmaruConfig();

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public boolean enable;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public boolean unbreakable;

    @Config.DefaultInt(4096)
    @Config.RequiresMcRestart
    public int durability;

    @Config.DefaultFloat(8)
    @Config.RangeFloat(min = 0, max = 200)
    @Config.Sync
    public float normalDamage;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public boolean damageCreativePlayers;

    @Config.DefaultFloat(8)
    @Config.RangeFloat(min = 0, max = 200)
    public float creativeDamage;
}
