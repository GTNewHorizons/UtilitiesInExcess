package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.ConversionRegistry;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.FluidTransferNodeLogic;
import net.minecraft.block.Block;
import net.minecraftforge.fluids.FluidStack;

public class FluidTransferNodePart extends BaseNodePart<FluidTransferNodeLogic, FluidStack>
{
    public FluidTransferNodePart(int meta) {
        super(meta);
    }

    @Override
    public Block getBlock() {
        return ModBlocks.TRANSFER_NODE.get();
    }

    @Override
    protected FluidTransferNodeLogic getLogic() {
        if (logic == null)
        {
            logic = new FluidTransferNodeLogic(this);
        }
        return logic;
    }

    @Override
    public String getType() {
        return ConversionRegistry.FluidTransferNode.getName();
    }
}
