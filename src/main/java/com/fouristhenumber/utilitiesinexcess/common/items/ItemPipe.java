package com.fouristhenumber.utilitiesinexcess.common.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemPipe extends ItemBlock
{
    public ItemPipe(Block block)
    {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }
}
