package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityFurnaceGeneratorPlus;

public class BlockFurnaceGeneratorPlus extends BlockBaseGenerator {

    public BlockFurnaceGeneratorPlus(String id) {
        super(id);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityFurnaceGeneratorPlus();
    }
}
