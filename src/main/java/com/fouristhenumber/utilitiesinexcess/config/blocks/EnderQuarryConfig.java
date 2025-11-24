package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = UtilitiesInExcess.MODID, category = "blocks.ender_quarry")
@Config.Comment("Ender Quarry Configuration")
@Config.LangKey("utilitiesinexcess.config.block.ender_quarry")
public class EnderQuarryConfig {

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
}
