package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

@Config(modid = UtilitiesInExcess.MODID, category = "blocks")
public class BlockConfig {

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(BlockConfig.class);
        ConfigurationManager.registerConfig(CursedEarthConfig.class);
    }

    @Config.DefaultBoolean(true)
    public static boolean enableFloatingBlock;

    @Config.DefaultBoolean(true)
    public static boolean enableCompressedCobblestone;

    @Config.DefaultBoolean(true)
    public static boolean enableCompressedDirt;

    @Config.DefaultBoolean(true)
    public static boolean enableCompressedSand;

    @Config.DefaultBoolean(true)
    public static boolean enableCompressedGravel;

    @Config.DefaultBoolean(true)
    public static boolean enableRedstoneClock;

    @Config.DefaultBoolean(true)
    public static boolean enableEtherealGlass;

    @Config.DefaultBoolean(true)
    public static boolean enableTrashCanItem;

    @Config.DefaultBoolean(true)
    public static boolean enableTrashCanFluid;

    @Config.DefaultBoolean(true)
    public static boolean enableDrum;

    @Config.DefaultBoolean(true)
    public static boolean enableMagicWood;

    @Config.DefaultBoolean(true)
    public static boolean enableMarginallyMaximisedChest;

    @Config.DefaultBoolean(true)
    public static boolean enableSignificantlyShrunkChest;

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

    @Config.Comment("Pure Love Configuration")
    public static final PureLove pureLove = new PureLove();

    @Config.LangKey("utilitiesinexcess.config.block.pure_love")
    public static class PureLove {

        @Config.DefaultBoolean(true)
        public boolean enablePureLove;

        @Config.Comment("Radius within which animals fall in love")
        @Config.DefaultInt(8)
        @Config.RangeInt(min = 1, max = 16)
        public int rangePureLove;
    }

    @Config.Comment("Sound Muffler Configuration")
    public static final SoundMuffler soundMuffler = new SoundMuffler();

    @Config.LangKey("utilitiesinexcess.config.block.sound_muffler")
    public static class SoundMuffler {

        @Config.DefaultBoolean(true)
        public boolean enableSoundMuffler;

        @Config.Comment("The volume reduction of sounds by the sound muffler. 0 = silent, 100 = normal level")
        @Config.DefaultInt(5)
        @Config.RangeInt(min = 0, max = 100)
        public int soundMufflerReduction;

        @Config.Comment("The radius a sound muffler operates in (as a square box)")
        @Config.DefaultInt(8)
        @Config.RangeInt(min = 1, max = 64)
        public int soundMufflerRange;
    }

    @Config.Comment("Rain Muffler Configuration")
    public static final RainMuffler rainMuffler = new RainMuffler();

    @Config.LangKey("utilitiesinexcess.config.block.rain_muffler")
    public static class RainMuffler {

        @Config.DefaultBoolean(true)
        public boolean enableRainMuffler;

        @Config.Comment("The radius a rain muffler operates in (as a square box)")
        @Config.DefaultInt(64)
        @Config.RangeInt(min = 1, max = 256)
        public int rainMufflerRange;
    }
}
