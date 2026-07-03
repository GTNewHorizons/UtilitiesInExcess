package com.fouristhenumber.utilitiesinexcess.config.items.invertedtools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.inverted_tools.gourmands_axe")
public class GourmandsAxeConfig {

    @Config.Ignore
    public static final GourmandsAxeConfig INSTANCE = new GourmandsAxeConfig();

    @Config.DefaultBoolean(true)
    @Config.Name("Enable")
    @Config.RequiresMcRestart
    @Config.Order(0)
    public boolean enable;

    @Config.DefaultBoolean(true)
    @Config.Name("Unbreakable")
    @Config.RequiresMcRestart
    @Config.Order(100)
    public boolean unbreakable;
    @Config.DefaultBoolean(true)
    @Config.Order(200)
    public boolean drainHp;
    @Config.DefaultFloat(15)
    @Config.RangeFloat(min = 0f, max = 100f)
    @Config.RequiresMcRestart
    @Config.Order(300)
    public float damageAgainstUndead;
    @Config.DefaultBoolean(true)
    @Config.Order(400)
    public boolean spawnParticles;
    @Config.DefaultInt(3)
    @Config.RangeInt(min = 0, max = 100)
    @Config.Order(500)
    public int maxHeal;
    @Config.DefaultInt(1)
    @Config.RangeInt(min = 0, max = 20)
    @Config.Order(600)
    public int foodGain;
    @Config.DefaultFloat(0.25f)
    @Config.RangeFloat(min = 0, max = 1)
    @Config.Order(700)
    public float saturationGain;
    @Config.DefaultBoolean(true)
    @Config.Order(800)
    public boolean useHungerAlways;
    @Config.DefaultBoolean(false)
    @Config.Order(900)
    public boolean voidMinedBlock;
}
