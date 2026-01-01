package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityTransferNode;

public class BlockRetrievalNode extends BlockTransferNodeBase {

    public BlockRetrievalNode() {
        super();
        setBlockName("retrieval_node");
    }

    @Override
    public String getTopIcon() {
        return "utilitiesinexcess:retrieval_node_top";
    }

    // TODO: new TE
    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityTransferNode();
    }
}
