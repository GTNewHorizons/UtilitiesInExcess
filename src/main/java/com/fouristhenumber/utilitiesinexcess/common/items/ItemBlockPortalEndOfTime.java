package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class ItemBlockPortalEndOfTime extends ItemBlock {

    public ItemBlockPortalEndOfTime(Block block) {
        super(block);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tile.temporal_gate.desc"));
    }
}
