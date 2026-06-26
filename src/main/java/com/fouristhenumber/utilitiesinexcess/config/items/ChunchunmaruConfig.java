package com.fouristhenumber.utilitiesinexcess.config.items;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.Chunchunmaru")
public class ChunchunmaruConfig {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enable;

    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean unbreakable;

    @Config.DefaultInt(4096)
    @Config.RequiresMcRestart
    public static int durability;

    @Config.DefaultFloat(8)
    @Config.RangeFloat(min = 0, max = 200)
    public static float normalDamage;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean damageCreativePlayers;

    @Config.DefaultFloat(8)
    @Config.RangeFloat(min = 0, max = 200)
    public static float creativeDamage;
}
