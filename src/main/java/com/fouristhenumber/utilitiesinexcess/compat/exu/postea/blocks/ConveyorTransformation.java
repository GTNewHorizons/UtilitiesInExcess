package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.gtnewhorizons.postea.api.IDExtenderCompat;
import com.gtnewhorizons.postea.utility.BlockConversionInfo;

public class ConveyorTransformation extends AbstractBlockTransformation {

    public ConveyorTransformation() {
        super();
        setDummyName("dummy_conveyor");
        setOldName("ExtraUtilities:conveyor");
    }

    public NBTTagCompound doItemTransformation(NBTTagCompound tag) {
        IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModBlocks.CONVEYOR.getItem()));
        tag.setInteger("Damage", 0);
        return tag;
    }

    public BlockConversionInfo doBlockTransformation(BlockConversionInfo blockConversionInfo, World world) {
        BlockConversionInfo blockConversionInfoNew = new BlockConversionInfo();
        int meta = blockConversionInfo.metadata;
        blockConversionInfoNew.blockID = Block.getIdFromBlock(ModBlocks.CONVEYOR.get());
        blockConversionInfoNew.metadata = meta - 2 >= 0 ? meta - 2 : meta + 2;

        return blockConversionInfoNew;
    }
}
