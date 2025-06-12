package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemDestructionPickaxe extends ItemPickaxe {
    public ItemDestructionPickaxe() {
        super(ToolMaterial.EMERALD);
        setTextureName("utilitiesinexcess:destructionPickaxe");
        setUnlocalizedName("destructionPickaxe");
        setMaxDamage(0);
    }

    // Equivalent to getEfficiencyOnBlock
    public float func_150893_a(ItemStack itemStack, Block block) {
        return block == Blocks.stone ? this.efficiencyOnProperMaterial * 5 : 0.5F;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.destructionPickaxe.desc.1"));
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.destructionPickaxe.desc.2"));
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.destructionPickaxe.desc.3"));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }
}
