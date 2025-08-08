package com.fouristhenumber.utilitiesinexcess.config.items.unstabletools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.unstable_tools.anti_particulate_shovel")
public class AntiParticulateShovelConfig {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enable;
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean unbreakable;
    @Config.DefaultBoolean(true)
    public static boolean breakFallingAbove;
    @Config.DefaultBoolean(true)
    public static boolean voidMinedBlocks;

}
