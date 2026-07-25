package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityEnergyTransferNode;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityItemTransferNode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTransferNodeEnergy extends BlockNodeBase
{

    public BlockTransferNodeEnergy() {
        super();
        setBlockName("transfer_node_energy");
    }

    @Override
    public int getRenderType() {
        return 0;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        if (metadata == 0)
        {
            return new TileEntityEnergyTransferNode(false);
        }
        return new TileEntityEnergyTransferNode(true);
    }
}
