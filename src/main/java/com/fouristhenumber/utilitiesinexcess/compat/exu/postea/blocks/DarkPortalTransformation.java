package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.ModBlocks;
import com.gtnewhorizons.postea.api.IDExtenderCompat;
import com.gtnewhorizons.postea.utility.BlockConversionInfo;

public class DarkPortalTransformation extends AbstractBlockTransformation {

    public DarkPortalTransformation() {
        super();
        setDummyName("dummy_dark_portal");
        setOldName("ExtraUtilities:dark_portal");
    }

    public NBTTagCompound doItemTransformation(NBTTagCompound tag) {
        int dmg = tag.getInteger("Damage");
        if (dmg == 2) {
            IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModBlocks.END_OF_TIME_PORTAL.getItem()));
        } else {
            IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModBlocks.UNDERWORLD_PORTAL.getItem()));
        }
        tag.setInteger("Damage", 0);
        return tag;
    }

    public BlockConversionInfo doBlockTransformation(BlockConversionInfo blockConversionInfo, World world) {
        BlockConversionInfo blockConversionInfoNew = new BlockConversionInfo();
        int dmg = blockConversionInfo.metadata;
        if (dmg == 2) {
            blockConversionInfoNew.blockID = Block.getIdFromBlock(ModBlocks.END_OF_TIME_PORTAL.get());
        } else {
            blockConversionInfoNew.blockID = Block.getIdFromBlock(ModBlocks.UNDERWORLD_PORTAL.get());
        }
        blockConversionInfoNew.metadata = 0;

        return blockConversionInfoNew;
    }
}
