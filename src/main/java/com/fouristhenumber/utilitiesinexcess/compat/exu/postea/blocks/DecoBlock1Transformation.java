package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.gtnewhorizons.postea.api.IDExtenderCompat;
import com.gtnewhorizons.postea.utility.BlockConversionInfo;

public class DecoBlock1Transformation extends AbstractBlockTransformation {

    public DecoBlock1Transformation() {
        super();
        setDummyName("dummy_decorativeBlock1");
        setOldName("ExtraUtilities:decorativeBlock1");
    }

    // TODO Add the remaining decorative block metas once they get implemented
    public NBTTagCompound doItemTransformation(NBTTagCompound tag) {
        int dmg = tag.getInteger("Damage");
        switch (dmg) {
            case 5:
                IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModBlocks.INVERTED_BLOCK.getItem()));
                break;
            case 8:
                IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModBlocks.MAGIC_WOOD.getItem()));
                break;
        }
        tag.setInteger("Damage", 0);
        return tag;
    }

    public BlockConversionInfo doBlockTransformation(BlockConversionInfo blockConversionInfo, World world) {
        BlockConversionInfo blockConversionInfoNew = new BlockConversionInfo();
        int meta = blockConversionInfo.metadata;
        if (meta == 5) {
            blockConversionInfoNew.blockID = Block.getIdFromBlock(ModBlocks.INVERTED_BLOCK.get());
        } else if (meta == 8) {
            blockConversionInfoNew.blockID = Block.getIdFromBlock(ModBlocks.MAGIC_WOOD.get());
        } else {
            return blockConversionInfo;
        }
        blockConversionInfoNew.metadata = 0;

        return blockConversionInfoNew;
    }
}
