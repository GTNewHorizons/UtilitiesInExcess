package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityRedstoneGenerator;

public class BlockRedstoneGenerator extends BlockBaseGenerator {

    @Override
    public String getGeneratorTEID() {
        return "TileEntityRedstoneGeneratorUIE";
    }

    public BlockRedstoneGenerator(String id, int mult) {
        super(id, mult);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityRedstoneGenerator();
    }
}
