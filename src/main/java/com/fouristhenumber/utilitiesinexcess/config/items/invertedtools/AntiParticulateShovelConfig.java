package com.fouristhenumber.utilitiesinexcess.config.items.invertedtools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.inverted_tools.anti_particulate_shovel")
public class AntiParticulateShovelConfig {

    @Config.Ignore
    public static final AntiParticulateShovelConfig INSTANCE = new AntiParticulateShovelConfig();

    @Config.Order(0)
    @Config.DefaultBoolean(true)
    @Config.Name("Enable")
    @Config.RequiresMcRestart
    public boolean enable;

    @Config.Order(100)
    @Config.DefaultBoolean(true)
    @Config.Name("Unbreakable")
    @Config.RequiresMcRestart
    public boolean unbreakable;

    @Config.Order(200)
    @Config.DefaultBoolean(true)
    public boolean breakFallingAbove;

    @Config.Order(300)
    @Config.DefaultBoolean(true)
    public boolean voidMinedBlocks;

}
