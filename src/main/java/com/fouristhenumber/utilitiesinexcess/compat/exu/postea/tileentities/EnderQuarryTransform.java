package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.gtnewhorizons.postea.api.TileEntityReplacementManager;
import com.gtnewhorizons.postea.utility.BlockInfo;

public class EnderQuarryTransform {

    public static BlockInfo transform(NBTTagCompound tag, World world, Chunk chunk) {
        return new BlockInfo(ModBlocks.VOID_QUARRY.get(), 0, EnderQuarryTransform::transformNBT);
    }

    private static NBTTagCompound transformNBT(NBTTagCompound oldTag) {
        NBTTagCompound tag = TileEntityReplacementManager.createTETagAtSamePosition("TileEntityVoidQuarryUIE", oldTag);
        tag.setInteger("Energy", oldTag.getInteger("Energy"));
        tag.setLong("brokenBlocks", oldTag.getLong("progress"));
        if (oldTag.getBoolean("finished")) tag.setString("state", "FINISHED");
        else {
            if (oldTag.getBoolean("started")) tag.setString("state", "RUNNING");
            else tag.setString("state", "STOPPED");
        }

        if (oldTag.getBoolean("started")) {
            NBTTagCompound areaTag = new NBTTagCompound();
            areaTag.setInteger("lowX", oldTag.getInteger("min_x") + 1);
            areaTag.setInteger("lowZ", oldTag.getInteger("min_z") + 1);
            areaTag.setInteger("highX", oldTag.getInteger("max_x") - 1);
            areaTag.setInteger("highZ", oldTag.getInteger("max_z") - 1);
            tag.setTag("area", areaTag);

            tag.setInteger("dx", (oldTag.getInteger("chunk_x") << 4) + oldTag.getInteger("dx"));
            tag.setInteger("dy", oldTag.getInteger("dy"));
            tag.setInteger("dz", (oldTag.getInteger("chunk_z") << 4) + oldTag.getInteger("dz"));

            tag.setInteger("lowerYBound", 1);
            tag.setInteger("upperYBound", oldTag.getInteger("chunk_y"));
        }

        int i = 0;
        NBTTagList itemTag = new NBTTagList();
        while (oldTag.hasKey("item_" + i)) {
            NBTTagCompound item = oldTag.getCompoundTag("item_" + i);
            // Void quarry expects shorts for items
            item.setShort("Count", item.getByte("Count"));
            itemTag.appendTag(item);
            i++;
        }
        tag.setTag("Items", itemTag);

        if (oldTag.hasKey("fluid")) {
            NBTTagList tankTag = new NBTTagList();
            NBTTagCompound oldFluid = oldTag.getCompoundTag("fluid");
            oldFluid.setByte("Slot", (byte) 0);
            tankTag.appendTag(oldFluid);
            tag.setTag("tanks", tankTag);
        }

        return tag;
    }
}
