package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.pipe;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityRationingPipe;

public class BlockRationingPipe extends BlockPipeBase
{
    public BlockRationingPipe() {
        super(Material.iron);
        this.setBlockName("rationing_pipe");
        this.setBlockTextureName("utilitiesinexcess:rationing_pipe");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityRationingPipe();
    }

}
