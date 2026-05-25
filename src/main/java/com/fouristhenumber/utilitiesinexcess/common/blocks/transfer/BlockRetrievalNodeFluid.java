package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityItemTransferNode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockRetrievalNodeFluid extends BlockTransferNodeBase {

    public BlockRetrievalNodeFluid() {
        super();
        setBlockName("retrieval_node_fluid");
    }

    @Override
    public String getTopIcon() {
        return "utilitiesinexcess:retrieval_node_fluid_top";
    }

    // TODO: new TE
    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityItemTransferNode();
    }
}
