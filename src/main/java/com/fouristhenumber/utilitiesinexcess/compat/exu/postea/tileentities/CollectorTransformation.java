package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.gtnewhorizons.postea.utility.BlockInfo;
import com.gtnewhorizons.postea.utility.PosteaUtilities;

public class CollectorTransformation {

    public static BlockInfo transform(NBTTagCompound _oldTag, World world, Chunk chunk) {
        return new BlockInfo(ModBlocks.COLLECTOR.get(), 0, (oldTag) -> {
            NBTTagCompound tag = PosteaUtilities.cleanseNBT("TileEntityCollectorUIE", oldTag);
            tag.setFloat("range", (float) (oldTag.getByte("Range") / 2));
            return tag;
        });
    }
}
