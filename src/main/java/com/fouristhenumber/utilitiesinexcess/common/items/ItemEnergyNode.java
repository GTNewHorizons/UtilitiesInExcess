package com.fouristhenumber.utilitiesinexcess.common.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.BlockTransferNodeEnergy;

public class ItemEnergyNode extends BaseTransferItemBlock {

    public ItemEnergyNode(Block block) {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile." + BlockTransferNodeEnergy.EnergyNodeType.values()[stack.getItemDamage()].getName();
    }
}
