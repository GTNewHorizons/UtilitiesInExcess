package com.fouristhenumber.utilitiesinexcess.config;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "blocks")
public class BlockConfig {

    @Config.DefaultBoolean(true)
    public static boolean enableFloatingBlock;

    @Config.DefaultBoolean(true)
    public static boolean enableCompressedCobblestone;

    @Config.DefaultBoolean(true)
    public static boolean enableRedstoneClock;

    @Config.DefaultBoolean(true)
    public static boolean enableEtherealGlass;

    @Config.DefaultBoolean(true)
    public static boolean enableTrashCanItem;
}
