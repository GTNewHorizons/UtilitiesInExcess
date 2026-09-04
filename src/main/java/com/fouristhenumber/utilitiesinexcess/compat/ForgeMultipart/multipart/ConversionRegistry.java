package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import codechicken.multipart.TMultiPart;
import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockPipe;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockRetrievalNode;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferBase;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferNode;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferNodeEnergy;
import net.minecraft.block.Block;

import javax.annotation.Nullable;

import java.util.function.Supplier;

import static com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart.UiEPartFactory.createUEMultiPart;

// Registry for non-FMP-only blocks that are able to be turned into FMP blocks on placement.
public enum ConversionRegistry
{
    TransferNode("utilitiesinexcess:transfer_node", BlockTransferNode.class, ModBlocks.TRANSFER_NODE::get),
    RetrievalNode("utilitiesinexcess:retrieval_node", BlockRetrievalNode.class, ModBlocks.RETRIEVAL_NODE::get),
    EnergyNode("utilitiesinexcess:transfer_node_energy", BlockTransferNodeEnergy.class, ModBlocks.TRANSFER_NODE_ENERGY::get),
    Pipe("utilitiesinexcess:transfer_pipe", BlockPipe.class, ModBlocks.TRANSFER_PIPE::get);

    private final String name;
    private final Class<? extends BlockTransferBase> block;
    private final Supplier<Block> blockSupplier;

    ConversionRegistry(String name, Class<? extends BlockTransferBase> block, Supplier<Block> supplier)
    {
        this.name = name;
        this.block = block;
        this.blockSupplier = supplier;
    }

    @Nullable
    public static TMultiPart getPartByBlock(final Block block, final int meta) {
        for (final ConversionRegistry registry : values()) {
            if (registry.block.isInstance(block))
            {
                return createUEMultiPart(meta, 0, registry.name);
            }
        }
        return null;
    }

    public String getName()
    {
        return this.name;
    }

    // for the sake of having everything in one place this is what I'm doing.
    public Block getBlock()
    {
        return blockSupplier.get();
    }
}
