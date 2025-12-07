package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityLavaGenerator;

public class BlockLavaGenerator extends BlockBaseGenerator {

    public BlockLavaGenerator(String id, int mult) {
        super(id, mult);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityLavaGenerator();
    }
}
