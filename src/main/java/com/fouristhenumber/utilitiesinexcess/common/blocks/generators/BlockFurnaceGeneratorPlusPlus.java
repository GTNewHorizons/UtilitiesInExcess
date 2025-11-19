package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityFurnaceGeneratorPlusPlus;

public class BlockFurnaceGeneratorPlusPlus extends BlockBaseGenerator {

    public BlockFurnaceGeneratorPlusPlus(String id) {
        super(id);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityFurnaceGeneratorPlusPlus();
    }
}
