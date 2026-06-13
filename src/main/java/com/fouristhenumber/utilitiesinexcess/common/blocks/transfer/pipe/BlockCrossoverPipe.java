package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.pipe;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityCrossoverPipe;

public class BlockCrossoverPipe extends BlockPipeBase
{
    public BlockCrossoverPipe() {
        super(Material.iron);
        this.setBlockName("crossover_pipe");
        this.setBlockTextureName("utilitiesinexcess:transfer_pipe");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCrossoverPipe();
    }
}
