package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityTransferNodeFluid;

public class BlockTransferNodeFluid extends BlockTransferNodeBase {

    public BlockTransferNodeFluid() {
        super();
        setBlockName("transfer_node_fluid");
    }

    @Override
    public String getTopIcon() {
        return "utilitiesinexcess:transfer_node_fluid_top";
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityTransferNodeFluid();
    }
}
