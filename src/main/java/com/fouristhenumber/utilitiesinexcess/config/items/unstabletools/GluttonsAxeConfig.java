package com.fouristhenumber.utilitiesinexcess.config.items.unstabletools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.unstable_tools.gluttons_axe")
public class GluttonsAxeConfig {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enable;
    @Config.DefaultBoolean(true)
    public static boolean drainHp;
    @Config.DefaultFloat(15)
    @Config.RangeFloat(min = 0f, max = 100f)
    public static float damageAgainstUndead;
    @Config.DefaultBoolean(true)
    public static boolean spawnParticles;
    @Config.DefaultInt(3)
    @Config.RangeInt(min = 0, max = 100)
    public static int maxHeal;
    @Config.DefaultInt(1)
    @Config.RangeInt(min = 0, max = 20)
    public static int foodGain;
    @Config.DefaultFloat(0.25f)
    @Config.RangeFloat(min = 0, max = 1)
    public static float saturationGain;
    @Config.DefaultBoolean(true)
    public static boolean useHungerAlways;
    @Config.DefaultBoolean(false)
    public static boolean voidMinedBlock;
}
