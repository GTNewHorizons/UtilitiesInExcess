package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "blocks.ender_lotus")
public class EnderLotusConfig {

    @Config.Ignore
    public static final EnderLotusConfig INSTANCE = new EnderLotusConfig();

    @Config.Order(0)
    @Config.DefaultBoolean(true)
    @Config.Name("Enable")
    @Config.RequiresMcRestart
    public boolean enableEnderLotus;

    @Config.Comment("Spawn Ender Lotuses in the End")
    @Config.Order(100)
    @Config.DefaultBoolean(true)
    public boolean spawnEnderLotusesInEnd;

    @Config.Comment("Average growth ticks to increase growth stage while planted on endspark")
    @Config.Order(200)
    @Config.DefaultInt(4)
    @Config.RangeInt(min = 1, max = 1000)
    public int growthTicksOnEndspark;

    @Config.Comment("Average growth ticks to increase growth stage while planted on endstone")
    @Config.Order(300)
    @Config.DefaultInt(25)
    @Config.RangeInt(min = 1, max = 1000)
    public int growthTicksOnEndstone;

    @Config.Comment("Average growth ticks to increase growth stage while planted on dirt/grass")
    @Config.Order(400)
    @Config.DefaultInt(100)
    @Config.RangeInt(min = 1, max = 1000)
    public int growthTicksOnDirt;

    @Config.Comment("Chance to drop an extra seed when harvested from a fully grown plant on endstone")
    @Config.Order(500)
    @Config.DefaultDouble(0.02)
    @Config.RangeDouble(min = 0.001, max = 1)
    public double extraSeedChanceOnEndstone;

    @Config.Comment("Chance to drop an extra seed when harvested from a fully grown plant on endspark")
    @Config.Order(600)
    @Config.DefaultDouble(0.20)
    @Config.RangeDouble(min = 0.001, max = 1)
    public double extraSeedChanceOnEndspark;

    @Config.Comment("Ender Lotuses hurt players and mobs when they walk on them")
    @Config.Order(700)
    @Config.DefaultBoolean(true)
    public boolean thornyLotuses;
}
