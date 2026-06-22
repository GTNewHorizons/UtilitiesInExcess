package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.gtnewhorizons.postea.api.BlockAccessCompat;
import com.gtnewhorizons.postea.api.TileEntityReplacementManager;
import com.gtnewhorizons.postea.utility.BlockInfo;

public class DrumTransformation {

    public static BlockInfo transform(NBTTagCompound tag, World world, Chunk chunk) {
        int[] blockIdAndMeta = BlockAccessCompat.getBlockIDAndMetaAtTE(tag, chunk);

        return new BlockInfo(
            blockIdAndMeta[1] == 0 ? ModBlocks.DRUM.get() : ModBlocks.BEDROCKIUM_DRUM.get(),
            0,
           DrumTransformation::transformNBT);
    }

    private static NBTTagCompound transformNBT(NBTTagCompound oldTag) {
        NBTTagCompound newTag = TileEntityReplacementManager.createTETagAtSamePosition("TileEntityDrumUIE", oldTag);
        NBTTagCompound tankTag = oldTag.getCompoundTag("tank");
        int capacity = tankTag.getInteger("capacity");
        tankTag.removeTag("capacity");
        newTag.setInteger("capacity", capacity);
        newTag.setTag("tank", tankTag);
        return newTag;
    }
}
