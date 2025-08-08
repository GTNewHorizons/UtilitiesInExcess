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
    @Config.DefaultStringList({ "minecraft:*stone", "minecraft:netherrack", "minecraft:*hardened_clay" })
    public static String[] includeEffective;
    @Config.DefaultStringList({})
    public static String[] excludeEffective;
    @Config.DefaultFloat(5)
    @Config.RangeFloat(min = 0, max = 100)
    public static float effectiveSpeedModifier;
    @Config.DefaultFloat(0.0625f)
    @Config.RangeFloat(min = 0, max = 100)
    public static float ineffectiveSpeedModifier;
}
