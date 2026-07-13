package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

public class BlockLapisAetheriusDyable extends BlockColoredWithLight {

    public BlockLapisAetheriusDyable() {
        super(ModBlocks.LAPIS_AETHERIUS.get());
        setBlockName("lapis_aetherius_dyeable");
        setLightOpacity(0);
        setHardness(1);
        setResistance(10F);
    }

    @Override
    public boolean ignoreBaseMeta() {
        return true;
    }

    @Override
    public boolean useDefaultNEIPage() {
        return false;
    }

    @Override
    public int getRenderType() {
        return UtilitiesInExcess.lapisAetheriusRenderID;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    public static class ItemLapisAetherius extends ItemBlockColoredWithLight {

        public ItemLapisAetherius(Block block) {
            super(block);
        }

        @Override
        public String getItemStackDisplayName(ItemStack p_77653_1_) {
            return StatCollector.translateToLocal("tile.lapis_aetherius_dyeable.name");
        }
    }
}
