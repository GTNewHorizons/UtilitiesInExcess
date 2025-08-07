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
    public static boolean enableDrum;

    @Config.DefaultBoolean(true)
    public static boolean enableMagicWood;

}
