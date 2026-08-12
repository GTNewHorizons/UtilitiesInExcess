package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;

public abstract class BlockTransferBase extends BlockContainer implements IConnectable
{
    protected BlockTransferBase(Material mat)
    {
        super(mat);
    }

}
