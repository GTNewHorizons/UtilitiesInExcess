package com.fouristhenumber.utilitiesinexcess.common.blocks.generators;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.generators.TileEntityPotionGenerator;

public class BlockPotionGenerator extends BlockBaseGenerator {

    public BlockPotionGenerator(String id, int mult) {
        super(id, mult);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityPotionGenerator();
    }
}
