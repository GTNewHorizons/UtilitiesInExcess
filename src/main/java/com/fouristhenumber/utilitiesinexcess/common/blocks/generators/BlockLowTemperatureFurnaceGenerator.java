package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityLowTemperatureFurnaceGenerator;

public class BlockLowTemperatureFurnaceGenerator extends BlockBaseGenerator {

    public BlockLowTemperatureFurnaceGenerator(String id) {
        super(id);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityLowTemperatureFurnaceGenerator();
    }
}
