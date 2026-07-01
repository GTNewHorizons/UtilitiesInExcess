package com.fouristhenumber.utilitiesinexcess.config.items.invertedtools;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "items.inverted_tools.anti_particulate_shovel")
public class AntiParticulateShovelConfig {

    @Config.Ignore
    public static final AntiParticulateShovelConfig INSTANCE = new AntiParticulateShovelConfig();

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public boolean enable;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public boolean unbreakable;
    @Config.DefaultBoolean(true)
    public boolean breakFallingAbove;
    @Config.DefaultBoolean(true)
    public boolean voidMinedBlocks;

}
