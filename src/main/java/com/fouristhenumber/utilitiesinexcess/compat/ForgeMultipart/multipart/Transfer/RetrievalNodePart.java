package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer;

import codechicken.lib.data.MCDataInput;
import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockNodeBase;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.ConversionRegistry;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.render.MetaOverrideWorld;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.FluidRetrievalNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.ItemRetrievalNodeLogic;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic.NetworkLogic;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

public class RetrievalNodePart extends BaseNodePart
{
    public RetrievalNodePart(int meta) {
        super(meta);
    }

    public RetrievalNodePart(MCDataInput packet)
    {
        super(packet.readInt());
        getLogic().readDesc(packet);
    }

    @Override
    public Block getBlock() {
        return ModBlocks.RETRIEVAL_NODE.get();
    }

    @Override
    protected NetworkLogic<?> getLogic() {
        if (logic == null)
        {
            logic = BlockNodeBase.getType(meta) == 0 ? new ItemRetrievalNodeLogic(this) : new FluidRetrievalNodeLogic(this);
        }
        return logic;
    }

    @Override
    public String getType() {
        return ConversionRegistry.RetrievalNode.getName();
    }
}
