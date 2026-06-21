package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks;

import com.gtnewhorizons.postea.api.IBlockTransformationHandler;
import com.gtnewhorizons.postea.utility.BlockConversionInfo;

public class EnderLilyTransformation implements IBlockTransformationHandler {
    // TODO: idk how to fix this
    /*
     * // This is NOT a direct mapping!!
     * // EXU's ender lily item is the block's ItemBlock, which is incompatible with our version which is a seed
     * // that plants the block,
     * public NBTTagCompound doItemTransformation(NBTTagCompound tag) {
     * IDExtenderCompat.setItemStackID(tag, Item.getIdFromItem(ModItems.ENDER_LOTUS_SEED.get()));
     * tag.setInteger("Damage", 0);
     * return tag;
     * }
     * public BlockConversionInfo doBlockTransformation(BlockConversionInfo blockConversionInfo, World world) {
     * BlockConversionInfo blockConversionInfoNew = new BlockConversionInfo();
     * blockConversionInfoNew.blockID = Block.getIdFromBlock(ModBlocks.ENDER_LOTUS.get());
     * blockConversionInfoNew.metadata = 0;
     * return blockConversionInfoNew;
     * }
     */

    @Override
    public boolean apply(BlockConversionInfo info) {
        return false;
    }

}
