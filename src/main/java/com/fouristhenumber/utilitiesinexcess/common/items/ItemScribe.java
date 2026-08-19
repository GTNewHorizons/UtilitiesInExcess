package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class ItemScribe extends Item {

    public ItemScribe() {
        setMaxStackSize(1);
        setMaxDamage(0);
        setUnlocalizedName("scribe");
        setTextureName("utilitiesinexcess:scribe");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("uie.desc.item.scribe.1"));
        super.addInformation(stack, player, tooltip, advanced);
    }
}
