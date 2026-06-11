package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "blocks.void_quarry")
@Config.Comment("Void Quarry Configuration")
@Config.LangKey("utilitiesinexcess.config.block.void_quarry")
public class VoidQuarryConfig {
    // TODO: Balance everything in here

    @Config.DefaultBoolean(true)
    public static boolean enableVoidQuarry;

    @Config.Comment("Default amount of blocks the quarry will mine upwards, added to its own y position.")
    @Config.DefaultInt(5)
    public static int voidQuarryDefaultTopPadding;

    @Config.Comment("Energy (RF) capacity of the machine.")
    @Config.DefaultInt(10_000_000)
    public static int voidQuarryEnergyStorage;

    @Config.Comment("Amount of fluid tanks, with one for each fluid type.")
    @Config.DefaultInt(2)
    public static int voidQuarryFluidTankAmount;

    @Config.Comment("Amount of fluid (in mB) that can be stored per tank.")
    @Config.DefaultInt(128_000)
    @Config.RangeInt(min = 16_000, max = 1_024_000)
    public static int voidQuarryFluidTankStorage;

    @Config.Comment("Base factor of RF that is used per operation. Is influenced by upgrades & block hardness.")
    @Config.DefaultInt(100)
    @Config.RangeInt(min = 100, max = 1_024_000)
    public static int voidQuarryBaseRFCost;

    @Config.DefaultStringList({ "COBBLE", "DIRT", "GLASS", "SNOW", "STONE" })
    @Config.DefaultString("COBBLE")
    @Config.Comment("Block type to replace mined blocks with if the world hole upgrade isn't present.")
    public static String voidQuarryReplaceBlock;

    @Config.DefaultInt(1)
    @Config.Comment("The amount of blocks the quarry tries to mine per tick, without speed upgrades.")
    public static int voidQuarryBaseSpeed;

    @Config.DefaultDouble(2D)
    @Config.Comment("The multiplier applied to the base mine speed.")
    public static double voidQuarrySpeed1Multiplier;

    @Config.DefaultDouble(8D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    public static double voidQuarrySpeed1EnergyMultiplier;

    @Config.DefaultDouble(4D)
    @Config.Comment("The multiplier applied to the base speed mine speed.")
    public static double voidQuarrySpeed2Multiplier;

    @Config.DefaultDouble(16D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    public static double voidQuarrySpeed2EnergyMultiplier;

    @Config.DefaultDouble(7D)
    @Config.Comment("The multiplier applied to the base speed mine speed.")
    public static double voidQuarrySpeed3Multiplier;

    @Config.DefaultDouble(32D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    public static double voidQuarrySpeed3EnergyMultiplier;

    @Config.DefaultDouble(12D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    public static double voidQuarryFortune1EnergyMultiplier;

    @Config.DefaultDouble(40D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    public static double voidQuarryFortune2EnergyMultiplier;

    @Config.DefaultDouble(100D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    public static double voidQuarryFortune3EnergyMultiplier;

    @Config.DefaultDouble(1.2D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    public static double voidQuarryWorldHoleEnergyMultiplier;

    @Config.DefaultDouble(8D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    public static double voidQuarrySilkTouchEnergyMultiplier;

    @Config.DefaultDouble(3D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    public static double voidQuarryFluidPumpEnergyMultiplier;
}
