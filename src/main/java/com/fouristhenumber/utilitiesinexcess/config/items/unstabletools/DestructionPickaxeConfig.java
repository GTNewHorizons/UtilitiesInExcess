package com.fouristhenumber.utilitiesinexcess.config.items.unstabletools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.unstable_tools.destruction_pickaxe")
public class DestructionPickaxeConfig {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enable;
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean unbreakable;
    @Config.DefaultBoolean(false)
    public static boolean voidMinedBlock;
}
