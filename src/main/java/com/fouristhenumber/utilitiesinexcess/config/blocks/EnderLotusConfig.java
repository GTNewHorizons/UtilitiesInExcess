package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "blocks.ender_lotus")
@Config.Comment("Ender Lotus Configuration")
@Config.LangKey("utilitiesinexcess.config.block.ender_lotus")
public class EnderLotusConfig {

    @Config.DefaultBoolean(true)
    public static boolean enableEnderLotus;

    @Config.Comment("Spawn Ender Lotuses in the End")
    @Config.DefaultBoolean(true)
    public static boolean spawnEnderLotusesInEnd;

    @Config.Comment("Average growth ticks to increase growth stage while planted on endstone")
    @Config.DefaultInt(25)
    public static int growthTicksOnEndstone;

    @Config.Comment("Average growth ticks to increase growth stage while planted on dirt/grass")
    @Config.DefaultInt(100)
    public static int growthTicksOnDirt;

    @Config.Comment("Chance to drop an extra seed when harvested from a fully grown plant on endstone")
    @Config.DefaultDouble(0.02)
    public static double extraSeedChanceOnEndstone;
}
