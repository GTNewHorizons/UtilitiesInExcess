package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityRedstoneGenerator;

public class BlockRedstoneGenerator extends BlockBaseGenerator {

    public BlockRedstoneGenerator(String id) {
        super(id);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityRedstoneGenerator();
    }
}
