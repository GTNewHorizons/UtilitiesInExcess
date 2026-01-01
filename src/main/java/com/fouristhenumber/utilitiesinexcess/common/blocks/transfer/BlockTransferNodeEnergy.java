package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityTransferNode;

public class BlockTransferNodeEnergy extends BlockTransferNodeBase {

    public BlockTransferNodeEnergy() {
        super();
        setBlockName("transfer_node_energy");
    }

    @Override
    public String getTopIcon() {
        return "utilitiesinexcess:transfer_node_energy_top";
    }

    // TODO: new TE
    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityTransferNode();
    }
}
