package com.fouristhenumber.utilitiesinexcess.common.items;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferNodeEnergy;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemEnergyNode extends BaseTransferItemBlock
{
    public ItemEnergyNode(Block block)
    {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return BlockTransferNodeEnergy.EnergyNodeType.values()[stack.getItemDamage()].getName();
    }
}
