package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityFoodGeneratorPlus;

public class BlockFoodGeneratorPlus extends BlockBaseGenerator {

    public BlockFoodGeneratorPlus(String id) {
        super(id);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityFoodGeneratorPlus();
    }
}
