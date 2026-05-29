package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.pipe;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferBase;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.pipe.TileEntityRationingPipe;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockRationingPipe extends BlockTransferBase
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
