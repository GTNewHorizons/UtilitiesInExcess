package com.fouristhenumber.utilitiesinexcess.config;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "blocks")
public class BlockConfig {

    @Config.DefaultBoolean(true)
    public static boolean enableFloatingBlock;

    @Config.DefaultBoolean(true)
    public static boolean enableWoodSpike;

    @Config.DefaultBoolean(true)
    public static boolean enableIronSpike;

    @Config.DefaultBoolean(true)
    public static boolean enableGoldSpike;

    @Config.DefaultBoolean(true)
    public static boolean enableDiamondSpike;
}
