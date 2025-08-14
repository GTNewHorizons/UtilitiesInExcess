package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config.Comment("RF Generators Configuration")
@Config.LangKey("utilitiesinexcess.config.item.generator")
@Config(modid = UtilitiesInExcess.MODID, category = "blocks.generator")
public class GeneratorConfig {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableLowTemperatureFurnaceGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int lowTemperatureFurnaceGeneratorRFCapacity;

    @Config.DefaultFloat(0.5F)
    @Config.RangeFloat(min = 0.0001F, max = 10000F)
    public static float lowTemperatureFurnaceGeneratorFuelUsageRatio;

    @Config.DefaultInt(5)
    @Config.RangeInt(min = 1)
    public static int lowTemperatureFurnaceGeneratorRFPerTick;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableFurnaceGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int furnaceGeneratorRFCapacity;

    @Config.DefaultFloat(0.3125F)
    @Config.RangeFloat(min = 0.0001F, max = 10000F)
    public static float furnaceGeneratorFuelUsageRatio;

    @Config.DefaultInt(40)
    @Config.RangeInt(min = 1)
    public static int furnaceGeneratorRFPerTick;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableHighTemperatureFurnaceGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int highTemperatureFurnaceGeneratorRFCapacity;

    @Config.DefaultFloat(0.0075F)
    @Config.RangeFloat(min = 0.0001F, max = 10000F)
    public static float highTemperatureFurnaceGeneratorFuelUsageRatio;

    @Config.DefaultInt(400)
    @Config.RangeInt(min = 1)
    public static int highTemperatureFurnaceGeneratorRFPerTick;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableLavaGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int lavaGeneratorRFCapacity;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableEnderGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int enderGeneratorRFCapacity;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableRedstoneGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int redstoneGeneratorRFCapacity;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableFoodGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int foodGeneratorRFCapacity;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enablePotionGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int potionGeneratorRFCapacity;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableSolarGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int solarGeneratorRFCapacity;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableTNTGenerator;

    @Config.DefaultInt(1_000_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int TNTGeneratorRFCapacity;

    @Config.DefaultInt(480_000)
    @Config.RangeInt(min = 1)
    public static int TNTGeneratorTNTFuelValue;

    @Config.DefaultInt(32_000)
    @Config.RangeInt(min = 1)
    public static int TNTGeneratorGunpowderFuelValue;

    @Config.DefaultBoolean(true)
    public static boolean TNTGeneratorExplosions;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enablePinkGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int pinkGeneratorRFCapacity;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean enableNetherStarGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public static int netherStarGeneratorRFCapacity;

}
