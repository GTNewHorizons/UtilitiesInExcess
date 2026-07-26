package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityEnergyTransferNode;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityItemTransferNode;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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

    @Override
    public int validWalkDirections(World world, int x, int y, int z, ForgeDirection fromDirection, int metadata, IWalkingComponent<?> walkingComponent) {
        return 0;
    }

    @Override
    public int getConnectionMask(IBlockAccess world, int x, int y, int z, int metadata) {
        return 0;
    }
}
