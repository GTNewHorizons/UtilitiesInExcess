package com.fouristhenumber.utilitiesinexcess.compat.tinkers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import tconstruct.library.ActiveToolMod;
import tconstruct.library.tools.ToolCore;

public class BedrockiumActiveToolMod extends ActiveToolMod {

    @Override
    public void updateTool(ToolCore tool, ItemStack stack, World world, Entity entity) {
        if (world.isRemote || !(entity instanceof EntityPlayer player) || !stack.hasTagCompound()) return;
        if (player.getHeldItem() != stack) return;

        NBTTagCompound toolTag = stack.getTagCompound()
            .getCompoundTag("InfiTool");

        if (toolTag.getBoolean("Heavy")) {
            player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 0, 1, true));
        }
    }
}
