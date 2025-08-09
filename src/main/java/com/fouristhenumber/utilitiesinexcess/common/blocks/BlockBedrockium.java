package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;

public class BlockBedrockium extends Block {

    public BlockBedrockium() {
        super(Material.iron);
        setBlockName("bedrockium_block");
        setBlockTextureName("utilitiesinexcess:bedrockium_block");
        setHardness(500F);
        setResistance(10000F);
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return false;
    }
}
