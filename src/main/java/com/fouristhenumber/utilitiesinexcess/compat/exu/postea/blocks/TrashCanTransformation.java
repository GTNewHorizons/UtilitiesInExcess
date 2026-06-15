package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.gtnewhorizons.postea.api.IDExtenderCompat;
import com.gtnewhorizons.postea.utility.BlockConversionInfo;

public class TrashCanTransformation extends AbstractBlockTransformation {

    public TrashCanTransformation() {
        super();
        setDummyName("dummy_trash_cans");
        setOldName("ExtraUtilities:trashcan");
    }

    public NBTTagCompound doItemTransformation(NBTTagCompound tag) {
        int dmg = tag.getInteger("Damage");
        switch (dmg) {
            case 1:
                IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModBlocks.TRASH_CAN_FLUID.getItem()));
                break;
            case 2:
                IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModBlocks.TRASH_CAN_ENERGY.getItem()));
                break;
            default:
                IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModBlocks.TRASH_CAN_ITEM.getItem()));
                break;
        }
        tag.setInteger("Damage", 0);
        return tag;
    }

    public BlockConversionInfo doBlockTransformation(BlockConversionInfo blockConversionInfo, World world) {
        BlockConversionInfo blockConversionInfoNew = new BlockConversionInfo();
        int dmg = blockConversionInfo.metadata;
        switch (dmg) {
            case 1:
                blockConversionInfoNew.blockID = Block.getIdFromBlock(ModBlocks.TRASH_CAN_FLUID.get());
                break;
            case 2:
                blockConversionInfoNew.blockID = Block.getIdFromBlock(ModBlocks.TRASH_CAN_ENERGY.get());
                break;
            default:
                blockConversionInfoNew.blockID = Block.getIdFromBlock(ModBlocks.TRASH_CAN_ITEM.get());
                break;
        }
        blockConversionInfoNew.metadata = 0;

        return blockConversionInfoNew;
    }
}
