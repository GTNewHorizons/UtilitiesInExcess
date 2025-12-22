package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "blocks.ender_quarry")
@Config.Comment("Ender Quarry Configuration")
@Config.LangKey("utilitiesinexcess.config.block.ender_quarry")
public class EnderQuarryConfig {
    // TODO: Balance everything in here

    @Config.DefaultBoolean(true)
    public static boolean enableEnderQuarry;

    @Config.Comment("Energy (RF) capacity of the machine.")
    @Config.DefaultInt(10_000_000)
    public static int enderQuarryEnergyStorage;

    @Config.Comment("Amount of fluid tanks, with one for each fluid type.")
    @Config.DefaultInt(2)
    public static int enderQuarryFluidTankAmount;

    @Config.Comment("Amount of fluid (in mB) that can be stored per tank.")
    @Config.DefaultInt(128_000)
    @Config.RangeInt(min = 16_000, max = 1_024_000)
    public static int enderQuarryFluidTankStorage;

    @Config.Comment("Base factor of RF that is used per operation. Is influenced by upgrades & block hardness.")
    @Config.DefaultInt(1_000)
    @Config.RangeInt(min = 100, max = 1_024_000)
    public static int enderQuarryBaseRFCost;

     @Config.DefaultStringList({"COBBLE", "DIRT", "GLASS", "SNOW", "STONE"})
     @Config.DefaultString("COBBLE")
     @Config.Comment("Block type to replace mined blocks with if the world hole upgrade isn't present.")
     public static String enderQuarryReplaceBlock;

     @Config.DefaultInt(400)
     @Config.Comment("The amount of blocks the quarry tries to mine per tick, without speed upgrades.")
     public static int enderQuarryBaseSpeed;


     @Config.DefaultDouble(2D)
     @Config.Comment("The multiplier applied to the base speed mine speed.")
     public static double enderQuarrySpeed1Multiplier;

     @Config.DefaultDouble(8D)
     @Config.Comment("The energy multiplier applied when the upgrade is active.")
     public static double enderQuarrySpeed1EnergyMultiplier;

     @Config.DefaultDouble(4D)
     @Config.Comment("The multiplier applied to the base speed mine speed.")
     public static double enderQuarrySpeed2Multiplier;

     @Config.DefaultDouble(16D)
     @Config.Comment("The energy multiplier applied when the upgrade is active.")
     public static double enderQuarrySpeed2EnergyMultiplier;

     @Config.DefaultDouble(7D)
     @Config.Comment("The multiplier applied to the base speed mine speed.")
     public static double enderQuarrySpeed3Multiplier;

     @Config.DefaultDouble(32D)
     @Config.Comment("The energy multiplier applied when the upgrade is active.")
     public static double enderQuarrySpeed3EnergyMultiplier;


    @Config.DefaultDouble(12D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    public static double enderQuarryFortune1EnergyMultiplier;

    @Config.DefaultDouble(40D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    public static double enderQuarryFortune2EnergyMultiplier;

    @Config.DefaultDouble(100D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    public static double enderQuarryFortune3EnergyMultiplier;


    @Config.DefaultDouble(1.2D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    public static double enderQuarryWorldHoleEnergyMultiplier;

    @Config.DefaultDouble(8D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    public static double enderQuarrySilkTouchEnergyMultiplier;

    @Config.DefaultDouble(3D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    public static double enderQuarryFluidPumpEnergyMultiplier;
}
