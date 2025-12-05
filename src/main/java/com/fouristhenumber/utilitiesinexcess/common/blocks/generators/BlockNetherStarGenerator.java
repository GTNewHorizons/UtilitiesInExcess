package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityNetherStarGenerator;

public class BlockNetherStarGenerator extends BlockBaseGenerator {

    @Override
    public String getGeneratorTEID() {
        return "TileEntityNetherStarGeneratorUIE";
    }

    public BlockNetherStarGenerator(String id, int mult) {
        super(id, mult);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityNetherStarGenerator();
    }
}
