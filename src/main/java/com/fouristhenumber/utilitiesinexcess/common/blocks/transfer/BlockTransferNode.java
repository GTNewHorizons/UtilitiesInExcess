package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityTransferNode;

public class BlockTransferNode extends BlockTransferNodeBase {

    public BlockTransferNode() {
        super();
        setBlockName("transfer_node");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityTransferNode();
    }
}
