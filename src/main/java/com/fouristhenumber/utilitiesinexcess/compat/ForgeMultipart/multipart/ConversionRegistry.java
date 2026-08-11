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

// Registry for non-FMP blocks that are able to be turned into FMP blocks on placement.
// I wanted to make only one part per type aka one retrieval one transfer, but realistically, it's much cleaner
// to just have 1 extra concrete class per type. I could get around it with factories and everything, but
// I generally believe that is going to make the code already more complicated and confusing than it is now...
public enum ConversionRegistry
{
    ItemTransferNode("item_transfer_node", BlockTransferNode.class, ModBlocks.TRANSFER_NODE::get),
    FluidTransferNode("fluid_transfer_node", BlockTransferNode.class, ModBlocks.TRANSFER_NODE::get),
    ItemRetrievalNode("item_retrieval_node", BlockRetrievalNode.class, ModBlocks.RETRIEVAL_NODE::get),
    FluidRetrievalNode("fluid_retrieval_node", BlockRetrievalNode.class, ModBlocks.RETRIEVAL_NODE::get),
    EnergyNode("energy_node", BlockTransferNodeEnergy.class, ModBlocks.TRANSFER_NODE_ENERGY::get),
    Pipe("pipe", BlockPipe.class, ModBlocks.TRANSFER_PIPE::get);

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
                return createUEMultiPart(false, meta, 0, registry.name);
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
