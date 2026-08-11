package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import codechicken.multipart.TMultiPart;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockPipe;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockRetrievalNode;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferBase;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferNode;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferNodeEnergy;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer.EnergyNodePart;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer.PipePart;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer.RetrievalNodePart;
import com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.Transfer.TransferNodePart;
import net.minecraft.block.Block;

import javax.annotation.Nullable;

import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.UiEPartFactory.createUEMultiPart;

// Registry for non-FMP blocks that are able to be turned into FMP blocks on placement.
public enum ConversionRegistry
{
    TransferNode("transfer_node", BlockTransferNode.class),
    RetrievalNode("retrieval_node", BlockRetrievalNode.class),
    EnergyNode("energy_node", BlockTransferNodeEnergy.class),
    Pipe("pipe", BlockPipe.class);

    private final String name;
    private final Class<? extends BlockTransferBase> block;

    ConversionRegistry(String name, Class<? extends BlockTransferBase> block)
    {
        this.name = name;
        this.block = block;
    }

    @Nullable
    public static TMultiPart getPartByBlock(final Block block, final int meta) {
        for (final ConversionRegistry registry : values()) {
            if (registry.block.isInstance(block)) {
                return createUEMultiPart(false, meta, 0, registry.name);
            }
        }
        return null;
    }

    public String getName()
    {
        return this.name;
    }
}
