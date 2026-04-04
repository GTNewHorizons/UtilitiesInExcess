package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import com.fouristhenumber.utilitiesinexcess.config.blocks.BlockConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockInverted extends Block {

    public BlockInverted() {
        super(Material.iron);
        setBlockName("inverted_block");
        setBlockTextureName("utilitiesinexcess:inverted_block");
        setHardness(0.5F);
        setResistance(150F);
    }

    @Override
    public boolean isOpaqueCube() {
        return BlockConfig.invertedBlockDoesXRay;
    }

    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return 1;
    }
}
