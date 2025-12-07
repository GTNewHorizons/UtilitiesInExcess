package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityLowTemperatureFurnaceGenerator;

public class BlockLowTemperatureFurnaceGenerator extends BlockBaseGenerator {

    public BlockLowTemperatureFurnaceGenerator(String id, int mult) {
        super(id, mult);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityLowTemperatureFurnaceGenerator();
    }
}
