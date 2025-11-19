package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityPinkGeneratorPlusPlus;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockPinkGeneratorPlusPlus extends BlockBaseGenerator{
    public BlockPinkGeneratorPlusPlus(String id) {super(id);}

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityPinkGeneratorPlusPlus();
    }
}
