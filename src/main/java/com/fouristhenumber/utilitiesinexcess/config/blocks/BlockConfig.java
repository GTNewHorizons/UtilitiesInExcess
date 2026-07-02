package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config.LangKey("utilitiesinexcess.config.blocks")
@Config(modid = UtilitiesInExcess.MODID, category = "blocks")
@Config.Order(0)
public class BlockConfig {

    @Config.Order(0)
    @Config.LangKey("utilitiesinexcess.config.block.spikes")
    public static final Spikes spikes = new Spikes();

    @Config.Order(100)
    @Config.LangKey("utilitiesinexcess.config.block.cursed_earth")
    public static final CursedEarthConfig cursedEarth = CursedEarthConfig.INSTANCE;

    @Config.Order(200)
    @Config.LangKey("utilitiesinexcess.config.block.muffler")
    public static final Mufflers mufflers = new Mufflers();

    @Config.Order(300)
    @Config.LangKey("utilitiesinexcess.config.block.chandelier")
    public static final Chandelier chandelier = new Chandelier();

    @Config.Order(400)
    @Config.LangKey("tile.giga_torch.name")
    public static final GigaTorch gigaTorch = new GigaTorch();

    @Config.Order(500)
    @Config.LangKey("tile.smart_pump.name")
    public static final SmartPump smartPump = new SmartPump();

    @Config.Order(600)
    @Config.LangKey("tile.void_quarry.name")
    public static final VoidQuarryConfig voidQuarry = VoidQuarryConfig.INSTANCE;

    @Config.Order(700)
    @Config.LangKey("tile.pure_love.name")
    public static final PureLove pureLove = new PureLove();

    @Config.Order(800)
    @Config.LangKey("tile.pacifists_bench.name")
    public static final PacifistsBench pacifistsBench = new PacifistsBench();

    @Config.Order(900)
    @Config.LangKey("tile.ender_lotus.name")
    public static final EnderLotusConfig enderLotus = EnderLotusConfig.INSTANCE;

    @Config.Order(1000)
    @Config.LangKey("utilitiesinexcess.config.block.colored_blocks")
    public static final ColoredBlocksConfig coloredBlocks = ColoredBlocksConfig.INSTANCE;

    public static class PureLove {

        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        @Config.Name("Enable")
        public boolean enablePureLove;

        @Config.Comment("Radius within which animals fall in love")
        @Config.DefaultInt(8)
        @Config.RangeInt(min = 1, max = 16)
        @Config.Name("Pure Love range")
        public int rangePureLove;
    }

    public static class Mufflers {

        @Config.Order(0)
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        public boolean enableSoundMuffler;

        @Config.Order(100)
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        public boolean enableRainMuffler;

        @Config.Order(200)
        @Config.Comment("The volume reduction of sounds by the sound muffler. 0 = silent, 100 = normal level")
        @Config.DefaultInt(5)
        @Config.RangeInt(min = 0, max = 100)
        public int soundMufflerReduction;

        @Config.Order(300)
        @Config.Comment("The radius a sound muffler operates in (as a square box)")
        @Config.DefaultInt(8)
        @Config.RangeInt(min = 1, max = 64)
        public int soundMufflerRange;

        @Config.Order(400)
        @Config.Comment("The radius a rain muffler operates in (as a square box)")
        @Config.DefaultInt(64)
        @Config.RangeInt(min = 1, max = 256)
        public int rainMufflerRange;
    }

    public static class Chandelier {

        @Config.Order(0)
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        @Config.Name("Enable")
        public boolean enableChandelier;

        @Config.Comment("The radius chandeliers blocks mob spawns (as a square box)")
        @Config.DefaultInt(16)
        @Config.RangeInt(min = 1, max = 256)
        @Config.Name("Range")
        public int chandelierLightRange;
    }

    public static class GigaTorch {

        @Config.Order(0)
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        @Config.Name("Enable")
        public boolean enableGigaTorch;

        @Config.Comment("The radius a giga torch blocks mob spawns (as a square box)")
        @Config.DefaultInt(64)
        @Config.RangeInt(min = 1, max = 256)
        @Config.Name("Range")
        public int gigaTorchRange;
    }

    public static class Spikes {

        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        @Config.Order(0)
        public boolean enableWoodenSpike;

        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        @Config.Order(100)
        public boolean enableIronSpike;

        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        @Config.Order(200)
        public boolean enableGoldSpike;

        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        @Config.Order(300)
        public boolean enableDiamondSpike;
    }

    public static class SmartPump {

        @Config.Order(0)
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        @Config.Name("Enable")
        public boolean enableSmartPump;

        @Config.DefaultInt(10000)
        @Config.RangeInt(min = 1)
        @Config.RequiresMcRestart
        public int smartPumpEnergyStorage;

        @Config.DefaultInt(100)
        @Config.RangeInt(min = 0)
        public int smartPumpEnergyUsePerBlock;

        @Config.DefaultInt(200)
        @Config.RangeInt(min = 1)
        @Config.Comment("The Smart Pump's cooldown (in ticks).")
        public int smartPumpStallCooldown;
    }

    public static class PacifistsBench {

        @Config.Order(0)
        @Config.DefaultBoolean(true)
        @Config.RequiresMcRestart
        @Config.Name("Enable")
        public boolean enablePacifistsBench;

        @Config.Order(100)
        @Config.DefaultInt(600)
        @Config.Name("Cooldown")
        @Config.Comment("The Pacifist's Bench cooldown (in ticks).")
        public int pacifistsBenchCooldown;

        @Config.Order(200)
        @Config.DefaultBoolean(false)
        @Config.Comment("Enable the Pacifist's Bench to work outside of peaceful mode.")
        public boolean pacifistsBenchInNonPeaceful;
    }

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableFloatingBlock;

    @Config.DefaultBoolean(false)
    @Config.Comment("If enabled, inverted block will have a similar X-Ray effect to Extra Utilities' Unstable Block.")
    @Config.RequiresMcRestart
    public static boolean invertedBlockDoesXRay;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableCompressedCobblestone;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableCompressedDirt;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableCompressedSand;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableCompressedGravel;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableRedstoneClock;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableEtherealGlass;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableTrashCanItem;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableTrashCanFluid;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableTrashCanEnergy;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableDrum;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMagicWood;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableEndspark;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableMarginallyMaximisedChest;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableSignificantlyShrunkChest;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableRadicallyReducedChest;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableBlockUpdateDetector;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableBlackoutCurtains;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableLapisAetherius;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableConveyor;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableCollector;

    @Config.DefaultBoolean(true)
    @Config.Comment({ "Enable the underworld portal block",
        "Can be disabled without disabling the underworld dimension" })
    @Config.RequiresMcRestart
    public static boolean enableUnderWorldPortal;

    @Config.DefaultBoolean(true)
    @Config.Comment({ "Enable the end of time portal block",
        "Can be disabled without disabling the end of time dimension" })
    @Config.RequiresMcRestart
    public static boolean enableEndOfTimePortal;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableEnderLocus;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableTradingPost;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableDecorativeGlass;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableDecorativeBlocks;
}
