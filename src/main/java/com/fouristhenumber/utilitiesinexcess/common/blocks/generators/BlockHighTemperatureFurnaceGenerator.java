package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityHighTemperatureFurnaceGenerator;

public class BlockHighTemperatureFurnaceGenerator extends BlockBaseGenerator {

    public BlockHighTemperatureFurnaceGenerator(String id) {
        super(id);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityHighTemperatureFurnaceGenerator();
    }
}
