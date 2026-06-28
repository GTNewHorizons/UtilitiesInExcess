package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.gtnewhorizons.postea.utility.BlockInfo;
import com.gtnewhorizons.postea.utility.PosteaUtilities;

public class QEDTransformation {

    public static BlockInfo transform(NBTTagCompound _oldTag, World world, Chunk chunk) {
        return new BlockInfo(ModBlocks.ENDER_LOCUS.get(), 0, QEDTransformation::transformNBT);
    }

    private static NBTTagCompound transformNBT(NBTTagCompound oldTag) {
        NBTTagCompound tag = PosteaUtilities.cleanseNBT("TileEntityEnderLocusUIE", oldTag);
        NBTTagList itemsTag = new NBTTagList();
        for (int i = 0; i < 9; i++) {
            NBTTagCompound exuSlotTag = oldTag.getCompoundTag("items_" + i);
            ItemStack exuSlotStack = ItemStack.loadItemStackFromNBT(exuSlotTag);
            if (exuSlotStack != null) {
                NBTTagCompound slotTag = new NBTTagCompound();
                slotTag.setByte("Slot", (byte) i);
                exuSlotStack.writeToNBT(slotTag);
                itemsTag.appendTag(slotTag);
            }
        }
        NBTTagCompound exuOutputTag = oldTag.getCompoundTag("output");
        ItemStack exuOutput = ItemStack.loadItemStackFromNBT(exuOutputTag);
        if (exuOutput != null) {
            NBTTagCompound slotTag = new NBTTagCompound();
            slotTag.setByte("Slot", (byte) 9);
            exuOutput.writeToNBT(slotTag);
            itemsTag.appendTag(slotTag);
        }
        tag.setTag("Items", itemsTag);
        int energy = oldTag.getInteger("Energy");
        tag.setInteger("craftingProgress", energy);
        if (energy > 0) tag.setBoolean("crafting", true);

        return tag;
    }
}
