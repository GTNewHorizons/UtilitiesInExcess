package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.gtnewhorizons.postea.api.BlockAccessCompat;
import com.gtnewhorizons.postea.api.TileEntityReplacementManager;
import com.gtnewhorizons.postea.utility.BlockInfo;

public class FilingCabinetTransformation {

    public static BlockInfo transform(NBTTagCompound tag, World world, Chunk chunk) {
        int meta = BlockAccessCompat.getBlockMetaAtTE(tag, chunk);

        return new BlockInfo(ModBlocks.FILING_CABINET.get(), meta < 8 ? 0 : 1, (t) -> transformNBT(t, meta));
    }

    private static NBTTagCompound transformNBT(NBTTagCompound oldTag, int meta) {
        NBTTagCompound tag = TileEntityReplacementManager.createTETagAtSamePosition(
            meta < 8 ? "TileEntityFilingCabinetBasicUIE" : "TileEntityFilingCabinetAdvancedUIE",
            oldTag);
        tag.setInteger("orientation", getOrientation(meta));

        NBTTagCompound inventoryTag = new NBTTagCompound();
        NBTTagList inventoryList = new NBTTagList();
        int i = 0;
        while (oldTag.hasKey("item_" + i)) {
            NBTTagCompound item = oldTag.getCompoundTag("item_" + i);
            item.setInteger("Count", item.getInteger("Size"));
            item.setInteger("Slot", i);
            inventoryList.appendTag(item);
            i++;
        }
        inventoryTag.setTag("Items", inventoryList);
        inventoryTag.setInteger("Size", meta < 8 ? 256 : 512);

        tag.setTag("Inventory", inventoryTag);
        return tag;
    }

    private static int getOrientation(int meta) {
        return switch (meta) {
            case 2, 8 -> 15;
            case 3, 9 -> 22;
            case 4, 10 -> 29;
            default -> 36;
        };
    }
}
