package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityItemTransferNode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTransferNodeEnergy extends BlockNodeBase {

    public BlockTransferNodeEnergy() {
        super();
        setBlockName("transfer_node_energy");
    }

    // TODO: new TE
    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityItemTransferNode();
    }
}
