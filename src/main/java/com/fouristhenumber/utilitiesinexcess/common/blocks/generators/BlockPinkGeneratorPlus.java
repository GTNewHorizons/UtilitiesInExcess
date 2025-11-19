package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityPinkGeneratorPlus;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockPinkGeneratorPlus extends BlockBaseGenerator{
    public BlockPinkGeneratorPlus(String id) {super(id);}

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityPinkGeneratorPlus();
    }
}
