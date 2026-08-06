package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "blocks.void_quarry")
public class VoidQuarryConfig {
    // TODO: Balance everything in here

    @Config.Ignore
    public static final VoidQuarryConfig INSTANCE = new VoidQuarryConfig();

    @Config.Order(0)
    @Config.DefaultBoolean(true)
    @Config.Name("Enable")
    @Config.RequiresMcRestart
    public boolean enableVoidQuarry;

    @Config.Order(100)
    @Config.Comment("Default amount of blocks the quarry will mine upwards, added to its own y position.")
    @Config.DefaultInt(5)
    @Config.RangeInt(min = 1, max = 64)
    @Config.RequiresMcRestart
    public int voidQuarryDefaultTopPadding;

    @Config.Order(200)
    @Config.Comment("Energy (RF) capacity of the machine.")
    @Config.DefaultInt(10_000_000)
    @Config.RangeInt(min = 256_000, max = 100_000_000)
    @Config.RequiresMcRestart
    public int voidQuarryEnergyStorage;

    @Config.Order(300)
    @Config.Comment("Amount of fluid tanks, with one for each fluid type.")
    @Config.DefaultInt(2)
    @Config.RangeInt(min = 1, max = 4)
    @Config.RequiresMcRestart
    public int voidQuarryFluidTankAmount;

    @Config.Order(400)
    @Config.Comment("Amount of fluid (in mB) that can be stored per tank. Will have +1000mb of buffer for overflow.")
    @Config.DefaultInt(127_000)
    @Config.RangeInt(min = 15_000, max = 1_023_000)
    @Config.RequiresMcRestart
    public int voidQuarryFluidTankStorage;

    @Config.Order(500)
    @Config.Comment("Base factor of RF that is used per operation. Is influenced by upgrades & block hardness.")
    @Config.DefaultInt(100)
    @Config.RangeInt(min = 100, max = 1_024_000)
    public int voidQuarryBaseRFCost;

    @Config.Order(600)
    @Config.DefaultStringList({ "COBBLE", "DIRT", "GLASS", "SNOW", "STONE" })
    @Config.DefaultString("COBBLE")
    @Config.Comment("Block type to replace mined blocks with if the world hole upgrade isn't present.")
    @Config.RequiresMcRestart
    public String voidQuarryReplaceBlock;

    @Config.Order(700)
    @Config.DefaultInt(2)
    @Config.Comment("The amount of blocks the quarry tries to mine per tick, without speed upgrades.")
    @Config.RangeInt(min = 1, max = 1048)
    @Config.RequiresMcRestart
    public int voidQuarryBaseSpeed;

    @Config.Order(800)
    @Config.DefaultDouble(2D)
    @Config.Comment("The multiplier applied to the base mine speed.")
    @Config.RangeDouble(min = 1.0, max = 80.0)
    @Config.RequiresMcRestart
    public double voidQuarrySpeed1Multiplier;

    @Config.Order(900)
    @Config.DefaultDouble(8D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    @Config.RangeDouble(min = 1.0, max = 80.0)
    @Config.RequiresMcRestart
    public double voidQuarrySpeed1EnergyMultiplier;

    @Config.Order(1000)
    @Config.DefaultDouble(4D)
    @Config.Comment("The multiplier applied to the base speed mine speed.")
    @Config.RangeDouble(min = 1.0, max = 160.0)
    @Config.RequiresMcRestart
    public double voidQuarrySpeed2Multiplier;

    @Config.Order(1100)
    @Config.DefaultDouble(16D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    @Config.RangeDouble(min = 1.0, max = 160.0)
    @Config.RequiresMcRestart
    public double voidQuarrySpeed2EnergyMultiplier;

    @Config.Order(1200)
    @Config.DefaultDouble(7D)
    @Config.Comment("The multiplier applied to the base speed mine speed.")
    @Config.RangeDouble(min = 1.0, max = 320.0)
    @Config.RequiresMcRestart
    public double voidQuarrySpeed3Multiplier;

    @Config.Order(1300)
    @Config.DefaultDouble(32D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    @Config.RangeDouble(min = 1.0, max = 320.0)
    @Config.RequiresMcRestart
    public double voidQuarrySpeed3EnergyMultiplier;

    @Config.Order(1400)
    @Config.DefaultDouble(12D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    @Config.RangeDouble(min = 1.0, max = 128.0)
    @Config.RequiresMcRestart
    public double voidQuarryFortune1EnergyMultiplier;

    @Config.Order(1500)
    @Config.DefaultDouble(40D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    @Config.RangeDouble(min = 1.0, max = 256.0)
    @Config.RequiresMcRestart
    public double voidQuarryFortune2EnergyMultiplier;

    @Config.Order(1600)
    @Config.DefaultDouble(100D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    @Config.RangeDouble(min = 1.0, max = 512.0)
    @Config.RequiresMcRestart
    public double voidQuarryFortune3EnergyMultiplier;

    @Config.Order(1700)
    @Config.DefaultDouble(1.2D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    @Config.RangeDouble(min = 1.0, max = 64.0)
    @Config.RequiresMcRestart
    public double voidQuarryWorldHoleEnergyMultiplier;

    @Config.Order(1800)
    @Config.DefaultDouble(8D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    @Config.RangeDouble(min = 1.0, max = 128.0)
    @Config.RequiresMcRestart
    public double voidQuarrySilkTouchEnergyMultiplier;

    @Config.Order(1900)
    @Config.DefaultDouble(3D)
    @Config.Comment("The energy multiplier applied when the upgrade is active.")
    @Config.RangeDouble(min = 1.0, max = 64.0)
    @Config.RequiresMcRestart
    public double voidQuarryFluidPumpEnergyMultiplier;
}
