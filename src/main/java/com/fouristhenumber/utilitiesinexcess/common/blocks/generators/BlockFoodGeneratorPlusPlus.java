package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityFoodGeneratorPlusPlus;

public class BlockFoodGeneratorPlusPlus extends BlockBaseGenerator {

    public BlockFoodGeneratorPlusPlus(String id) {
        super(id);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityFoodGeneratorPlusPlus();
    }
}
