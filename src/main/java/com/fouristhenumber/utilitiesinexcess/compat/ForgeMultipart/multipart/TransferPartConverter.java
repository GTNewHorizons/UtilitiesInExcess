package com.fouristhenumber.utilitiesinexcess.compat.ForgeMultipart.multipart;

import codechicken.lib.vec.BlockCoord;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class TransferPartConverter implements MultiPartRegistry.IPartConverter
{

    @Override
    public Iterable<Block> blockTypes() {
        return null;
    }

    @Override
    public TMultiPart convert(World world, BlockCoord pos) {
        return null;
    }
}
