package com.fouristhenumber.utilitiesinexcess.common.items.tools;

import java.util.List;

import com.fouristhenumber.utilitiesinexcess.config.items.ItemConfig;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.fouristhenumber.utilitiesinexcess.config.items.unstabletools.DestructionPickaxeConfig;

public class ItemDestructionPickaxe extends ItemPickaxe {

    public ItemDestructionPickaxe() {
        super(ToolMaterial.EMERALD);
        setTextureName("utilitiesinexcess:destruction_pickaxe");
        setUnlocalizedName("destruction_pickaxe");
        if (DestructionPickaxeConfig.unbreakable) setMaxDamage(0);
    }

    // Equivalent to getEfficiencyOnBlock
    @Override
    public float func_150893_a(ItemStack itemStack, Block block) {
        // Why specifically 0.5?
        // Maybe change it from stone to other materials too, ie sandstone, etr stone variations,etc
        return block == Blocks.stone ? this.efficiencyOnProperMaterial * 5 : 0.5F;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        if (!ItemConfig.shiftForDescription || GuiScreen.isShiftKeyDown()) {
            tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.destruction_pickaxe.desc.1"));
            tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.destruction_pickaxe.desc.2"));
            tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.destruction_pickaxe.desc.3"));
        } else tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("shift_for_description"));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    // Unbreakable
    @Override
    public boolean isDamageable() {
        if (DestructionPickaxeConfig.unbreakable) return false;
        return super.isDamageable();
    }

    @Override
    public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
        if (DestructionPickaxeConfig.unbreakable) return false;
        return super.getIsRepairable(stack, repairMaterial);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (DestructionPickaxeConfig.unbreakable) return false;
        return super.showDurabilityBar(stack);
    }
    //
}
