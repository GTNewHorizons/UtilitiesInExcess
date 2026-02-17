package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.gtnewhorizons.postea.api.IDExtenderCompat;
import com.gtnewhorizons.postea.utility.BlockConversionInfo;

public class CompressedBlocksTransformation extends AbstractBlockTransformation {

    public CompressedBlocksTransformation() {
        super();
        setDummyName("dummy_compressed_blocks");
        setOldName("ExtraUtilities:cobblestone_compressed");
    }

    public NBTTagCompound doItemTransformation(NBTTagCompound tag) {
        int dmg = tag.getInteger("Damage");
        if (dmg < 8) {
            IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModBlocks.COMPRESSED_COBBLESTONE.getItem()));
        } else if (dmg < 12) {
            IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModBlocks.COMPRESSED_DIRT.getItem()));
            tag.setInteger("Damage", dmg - 8);
        } else if (dmg < 14) {
            IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModBlocks.COMPRESSED_GRAVEL.getItem()));
            tag.setInteger("Damage", dmg - 12);
        } else {
            IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModBlocks.COMPRESSED_SAND.getItem()));
            tag.setInteger("Damage", dmg - 14);
        }
        return tag;
    }

    public BlockConversionInfo doBlockTransformation(BlockConversionInfo blockConversionInfo, World world) {
        BlockConversionInfo blockConversionInfoNew = new BlockConversionInfo();
        int dmg = blockConversionInfo.metadata;
        if (dmg < 8) {
            blockConversionInfoNew.blockID = Block.getIdFromBlock(ModBlocks.COMPRESSED_COBBLESTONE.get());
            blockConversionInfoNew.metadata = blockConversionInfo.metadata;
        } else if (dmg < 12) {
            blockConversionInfoNew.blockID = Block.getIdFromBlock(ModBlocks.COMPRESSED_DIRT.get());
            blockConversionInfoNew.metadata = blockConversionInfo.metadata - 8;
        } else if (dmg < 14) {
            blockConversionInfoNew.blockID = Block.getIdFromBlock(ModBlocks.COMPRESSED_GRAVEL.get());
            blockConversionInfoNew.metadata = blockConversionInfo.metadata - 12;
        } else {
            blockConversionInfoNew.blockID = Block.getIdFromBlock(ModBlocks.COMPRESSED_SAND.get());
            blockConversionInfoNew.metadata = blockConversionInfo.metadata - 14;
        }

        return blockConversionInfoNew;
    }
}
