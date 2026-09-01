package com.fouristhenumber.utilitiesinexcess.common.blocks.transfer;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;

public abstract class BlockTransferBase extends BlockContainer implements IConnectable
{
    protected BlockTransferBase(Material mat)
    {
        super(mat);
        this.setHardness(0.1F);
    }

    public boolean canHarvestBlock(EntityPlayer player, int meta)
    {
        return true;
    }
}
