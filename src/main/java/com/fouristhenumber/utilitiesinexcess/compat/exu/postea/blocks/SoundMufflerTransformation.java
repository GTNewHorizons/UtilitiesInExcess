package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.gtnewhorizons.postea.api.IDExtenderCompat;
import com.gtnewhorizons.postea.utility.BlockConversionInfo;

public class SoundMufflerTransformation extends AbstractBlockTransformation {

    public SoundMufflerTransformation() {
        super();
        setDummyName("dummy_sound_muffler");
        setOldName("ExtraUtilities:sound_muffler");
    }

    public NBTTagCompound doItemTransformation(NBTTagCompound tag) {
        int dmg = tag.getInteger("Damage");
        switch (dmg) {
            case 0:
                IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModBlocks.SOUND_MUFFLER.getItem()));
                break;
            case 1:
                IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModBlocks.RAIN_MUFFLER.getItem()));
                break;
        }
        tag.setInteger("Damage", 0);
        return tag;
    }

    public BlockConversionInfo doBlockTransformation(BlockConversionInfo blockConversionInfo, World world) {
        BlockConversionInfo blockConversionInfoNew = new BlockConversionInfo();
        int meta = blockConversionInfo.metadata;
        if (meta == 1) {
            blockConversionInfoNew.blockID = Block.getIdFromBlock(ModBlocks.RAIN_MUFFLER.get());
        } else {
            blockConversionInfoNew.blockID = Block.getIdFromBlock(ModBlocks.SOUND_MUFFLER.get());
        }
        blockConversionInfoNew.metadata = 0;

        return blockConversionInfoNew;
    }
}
