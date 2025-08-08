package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "blocks.cursed_earth")
@Config.Comment("Cursed Earth Configuration")
@Config.LangKey("utilitiesinexcess.config.block.cursed_earth")
public class CursedEarthConfig {

    @Config.DefaultBoolean(true)
    public static boolean enableCursedEarth;

    @Config.Comment("Chance that a mob is spawned on a Cursed Earth block on a random tick.")
    @Config.DefaultInt(40)
    @Config.RangeInt(min = 0, max = 100)
    public static int cursedEarthSpawnRate;
}
