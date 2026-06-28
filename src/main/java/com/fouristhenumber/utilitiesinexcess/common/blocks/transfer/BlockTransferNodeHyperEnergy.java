package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityItemTransferNode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTransferNodeHyperEnergy extends BlockNodeBase {

    public BlockTransferNodeHyperEnergy() {
        super();
        setBlockName("transfer_node_hyper_energy");
    }

    @Override
    public String getTopIcon() {
        return "utilitiesinexcess:transfer_node_hyper_energy_top";
    }

    // TODO: new TE
    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityItemTransferNode();
    }
}
