package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "blocks.cursed_earth")
@Config.Comment("Cursed Earth Configuration")
@Config.LangKey("utilitiesinexcess.config.block.cursed_earth")
public class CursedEarthConfig {

    @Config.Ignore
    public static final CursedEarthConfig INSTANCE = new CursedEarthConfig();

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public boolean enableCursedEarth;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public boolean enableBlessedEarth;

    @Config.Comment("Should blessed earth burn in darkness like cursed earth burns in the light")
    @Config.DefaultBoolean(false)
    public boolean enableBlessedEarthBurn;

    @Config.Comment("Should blessed and cursed earth spread to nearby grass")
    @Config.DefaultBoolean(true)
    public boolean cursedEarthSpreads;

    @Config.Comment("Should blessed and cursed earth explode upon contacting each other. cursedEarthSpreads must be true!")
    @Config.DefaultBoolean(true)
    public boolean volatileReaction;

    @Config.Comment("Chance that a mob is spawned on a Cursed Earth block on a random tick.")
    @Config.DefaultInt(40)
    @Config.RangeInt(min = 0, max = 100)
    public int cursedEarthSpawnRate;

    @Config.Comment("Chance that a mob is spawned on a Blessed Earth block on a random tick.")
    @Config.DefaultInt(40)
    @Config.RangeInt(min = 0, max = 100)
    public int blessedEarthSpawnRate;
}
