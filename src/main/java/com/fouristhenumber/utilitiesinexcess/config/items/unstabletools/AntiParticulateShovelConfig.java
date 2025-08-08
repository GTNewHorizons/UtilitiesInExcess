package com.fouristhenumber.utilitiesinexcess.config.items.unstabletools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.unstable_tools.anti_particulate_shovel")
public class AntiParticulateShovelConfig {
    @Config.DefaultBoolean(true)
    public static boolean enable;

}
