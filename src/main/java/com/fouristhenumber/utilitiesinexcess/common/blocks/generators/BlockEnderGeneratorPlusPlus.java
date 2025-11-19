package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityEnderGeneratorPlusPlus;

public class BlockEnderGeneratorPlusPlus extends BlockBaseGenerator {

    public BlockEnderGeneratorPlusPlus(String id) {
        super(id);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityEnderGeneratorPlusPlus();
    }
}
