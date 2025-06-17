package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class ItemEthericSword extends ItemSword {

    public ItemEthericSword() {
        super(ToolMaterial.EMERALD);
        setTextureName("utilitiesinexcess:etheric_sword");
        setUnlocalizedName("etheric_sword");
        setMaxDamage(0);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.etheric_sword.desc.1"));
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.etheric_sword.desc.2"));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }
}
