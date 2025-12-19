package com.fouristhenumber.utilitiesinexcess.config.blocks;

import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.gtnewhorizon.gtnhlib.config.Config;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

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

    @Config.Comment("Base factor of RF that is used per operation. Is influenced by upgrades & block hardness.")
    @Config.DefaultInt(1_000)
    @Config.RangeInt(min = 100, max = 1_024_000)
    public static int enderQuarryBaseRFCost;

    @Config.DefaultEnum("COBBLE")
    @Config.Comment("Block type to replace mined blocks with if the world hole upgrade isn't present.")
    public static EnderQuarryReplaceBlock enderQuarryReplaceBlock;


    public enum EnderQuarryReplaceBlock {
        COBBLE(Blocks.cobblestone),
        DIRT(Blocks.dirt),
        GLASS(Blocks.glass),
        SNOW(Blocks.snow),
        STONE(Blocks.stone);

        public final Block block;

        EnderQuarryReplaceBlock(Block block) {
            this.block = block;
        }
    }
}
