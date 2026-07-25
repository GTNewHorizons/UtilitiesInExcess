package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config.LangKey("uie.config.generators")
@Config(modid = UtilitiesInExcess.MODID, category = "generators")
@Config.Order(200)
public class GeneratorConfig {

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Order(0)
    public static boolean enableLowTemperatureFurnaceGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    @Config.Order(100)
    public static int lowTemperatureFurnaceGeneratorRFCapacity;

    @Config.DefaultFloat(0.5F)
    @Config.RangeFloat(min = 0.0001F, max = 10000F)
    @Config.Order(200)
    public static float lowTemperatureFurnaceGeneratorFuelUsageRatio;

    @Config.DefaultInt(5)
    @Config.RangeInt(min = 1)
    @Config.Order(300)
    public static int lowTemperatureFurnaceGeneratorRFPerTick;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Order(400)
    public static boolean enableFurnaceGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    @Config.Order(500)
    public static int furnaceGeneratorRFCapacity;

    @Config.DefaultFloat(0.3125F)
    @Config.RangeFloat(min = 0.0001F, max = 10000F)
    @Config.Order(600)
    public static float furnaceGeneratorFuelUsageRatio;

    @Config.DefaultInt(40)
    @Config.RangeInt(min = 1)
    @Config.Order(700)
    public static int furnaceGeneratorRFPerTick;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Order(800)
    public static boolean enableHighTemperatureFurnaceGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    @Config.Order(900)
    public static int highTemperatureFurnaceGeneratorRFCapacity;

    @Config.DefaultFloat(0.0075F)
    @Config.RangeFloat(min = 0.0001F, max = 10000F)
    @Config.Order(1000)
    public static float highTemperatureFurnaceGeneratorFuelUsageRatio;

    @Config.DefaultInt(400)
    @Config.RangeInt(min = 1)
    @Config.Order(1100)
    public static int highTemperatureFurnaceGeneratorRFPerTick;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Order(1200)
    public static boolean enableLavaGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    @Config.Order(1300)
    public static int lavaGeneratorRFCapacity;

    @Config.DefaultInt(40)
    @Config.RangeInt(min = 1)
    @Config.Order(1400)
    public static int lavaGeneratorRFPerTick;

    @Config.DefaultInt(50)
    @Config.RangeInt(min = 1)
    @Config.Order(1500)
    public static int lavaGeneratorFuelBurnTime;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Order(1600)
    public static boolean enableEnderGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    @Config.Order(1700)
    public static int enderGeneratorRFCapacity;

    @Config.DefaultInt(40)
    @Config.RangeInt(min = 1)
    @Config.Order(1800)
    public static int enderGeneratorRFPerTick;

    @Config.DefaultInt(800)
    @Config.RangeInt(min = 0)
    @Config.Order(1900)
    public static int enderGeneratorEnderPearlBurnTime;

    @Config.DefaultInt(3000)
    @Config.RangeInt(min = 0)
    @Config.Order(2000)
    public static int enderGeneratorEnderEyeBurnTime;

    @Config.DefaultInt(12000)
    @Config.RangeInt(min = 0)
    @Config.Order(2100)
    public static int enderGeneratorEnderLotusSeedBurnTime;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Order(2200)
    public static boolean enableRedstoneGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    @Config.Order(2300)
    public static int redstoneGeneratorRFCapacity;

    @Config.DefaultInt(80)
    @Config.RangeInt(min = 1)
    @Config.Order(2400)
    public static int redstoneGeneratorRFPerTick;

    @Config.DefaultInt(320)
    @Config.RangeInt(min = 1)
    @Config.Order(2500)
    public static int redstoneGeneratorFuelBurnTime;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Order(2600)
    public static boolean enableFoodGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    @Config.Order(2700)
    public static int foodGeneratorRFCapacity;

    @Config.DefaultInt(4)
    @Config.RangeInt(min = 1)
    @Config.Order(2800)
    public static int foodGeneratorRFMultiplier;

    @Config.DefaultInt(900)
    @Config.RangeInt(min = 1)
    @Config.Order(2900)
    public static int foodGeneratorBurnTimeMultiplier;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Order(3000)
    public static boolean enablePotionGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    @Config.Order(3100)
    public static int potionGeneratorRFCapacity;

    @Config.DefaultInt(20)
    @Config.RangeInt(min = 1)
    @Config.Order(3200)
    public static int potionGeneratorRFMultiplier;

    @Config.DefaultInt(800)
    @Config.RangeInt(min = 0)
    @Config.Order(3300)
    public static int potionGeneratorBurnTime;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Order(3400)
    public static boolean enableSolarGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    @Config.Order(3500)
    public static int solarGeneratorRFCapacity;

    @Config.DefaultInt(40)
    @Config.RangeInt(min = 0)
    @Config.Order(3600)
    public static int solarGeneratorTopGeneration;

    @Config.DefaultInt(40)
    @Config.RangeInt(min = 0)
    @Config.Order(3700)
    public static int solarGeneratorNoSkyGeneration;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Order(3800)
    public static boolean enableTNTGenerator;

    @Config.DefaultInt(1_000_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    @Config.Order(3900)
    public static int TNTGeneratorRFCapacity;

    @Config.DefaultInt(480_000)
    @Config.RangeInt(min = 0)
    @Config.Order(4000)
    public static int TNTGeneratorTNTFuelValue;

    @Config.DefaultInt(32_000)
    @Config.RangeInt(min = 0)
    @Config.Order(4100)
    public static int TNTGeneratorGunpowderFuelValue;

    @Config.DefaultBoolean(true)
    @Config.Order(4200)
    public static boolean TNTGeneratorExplosions;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Order(4300)
    public static boolean enablePinkGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    @Config.Order(4400)
    public static int pinkGeneratorRFCapacity;

    @Config.DefaultInt(40)
    @Config.RangeInt(min = 1)
    @Config.Order(4500)
    public static int pinkGeneratorRFPerTick;

    @Config.DefaultInt(400)
    @Config.RangeInt(min = 1)
    @Config.Order(4600)
    public static int pinkGeneratorFuelBurnTime;

    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    @Config.Order(4700)
    public static boolean enableNetherStarGenerator;

    @Config.DefaultInt(100_000)
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    @Config.Order(4800)
    public static int netherStarGeneratorRFCapacity;

    @Config.DefaultInt(40_000)
    @Config.RangeInt(min = 1)
    @Config.Order(4900)
    public static int netherStarGeneratorRFPerTick;

    @Config.DefaultInt(2400)
    @Config.RangeInt(min = 1)
    @Config.Order(5000)
    public static int netherStarGeneratorFuelBurnTime;

}
