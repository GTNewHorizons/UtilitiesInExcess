package com.fouristhenumber.utilitiesinexcess.common.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemGluttonsAxe extends ItemAxe {

    public ItemGluttonsAxe() {
        super(ToolMaterial.EMERALD);
        setTextureName("utilitiesinexcess:gluttons_axe");
        setUnlocalizedName("gluttons_axe");
        setMaxDamage(0);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.gluttons_axe.desc.1"));
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.gluttons_axe.desc.2"));
        tooltip.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("item.gluttons_axe.desc.3"));
        super.addInformation(stack, player, tooltip, p_77624_4_);
    }

    // Restore hunger every 2 seconds
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (entity instanceof EntityPlayer player && isSelected && world.getTotalWorldTime() % 40 == 0) {
            player.getFoodStats()
                .addStats(2, 0.4F);
        }
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z,
        EntityLivingBase entity) {
        return true;
    }
}
