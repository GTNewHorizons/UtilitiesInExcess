package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockInverted extends Block {

    public BlockInverted() {
        super(Material.iron);
        setBlockName("inverted_block");
        setBlockTextureName("utilitiesinexcess:inverted_block");
        setHardness(0.5F);
        setResistance(150F);
    }
}
