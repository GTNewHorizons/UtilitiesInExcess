package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.ConversionRegistry;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.FluidRetrievalNodeLogic;
import net.minecraft.block.Block;
import net.minecraftforge.fluids.FluidStack;

public class FluidRetrievalNodePart extends BaseNodePart<FluidRetrievalNodeLogic, FluidStack>
{
    public FluidRetrievalNodePart(int meta) {
        super(meta);
    }

    @Override
    public Block getBlock() {
        return ModBlocks.RETRIEVAL_NODE.get();
    }

    @Override
    protected FluidRetrievalNodeLogic getLogic() {
        if (logic == null)
        {
            logic = new FluidRetrievalNodeLogic(this);
        }
        return logic;
    }

    @Override
    public String getType() {
        return ConversionRegistry.FluidRetrievalNode.getName();
    }
}
