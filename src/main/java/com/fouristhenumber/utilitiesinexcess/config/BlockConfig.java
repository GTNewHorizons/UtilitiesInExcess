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
    public static boolean enableDrum;

    @Config.DefaultBoolean(true)
    public static boolean enableMagicWood;

    @Config.Comment("Cursed Earth Configuration")
    public static final CursedEarth cursedEarth = new CursedEarth();

    @Config.LangKey("utilitiesinexcess.config.block.cursed_earth")
    public static class CursedEarth {

        @Config.DefaultBoolean(true)
        public boolean enableCursedEarth;

        @Config.Comment("Chance that a mob is spawned on a Cursed Earth block on a random tick.")
        @Config.DefaultInt(40)
        @Config.RangeInt(min = 0, max = 100)
        public int cursedEarthSpawnRate;
    }
}
