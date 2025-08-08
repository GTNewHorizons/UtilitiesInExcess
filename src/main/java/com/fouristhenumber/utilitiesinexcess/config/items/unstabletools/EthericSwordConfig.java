package com.fouristhenumber.utilitiesinexcess.config.items.unstabletools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.unstable_tools.etheric_sword")
public class EthericSwordConfig {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enable;

    @Config.DefaultFloat(4)
    @Config.RangeFloat(min = 0, max = 20)
    public static float normalDamage;
    @Config.DefaultFloat(3)
    @Config.RangeFloat(min = 0, max = 20)
    public static float magicDamage;
}
