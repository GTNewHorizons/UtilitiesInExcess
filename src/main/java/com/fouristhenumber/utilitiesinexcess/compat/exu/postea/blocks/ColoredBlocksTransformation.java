package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks;

import net.minecraft.block.Block;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;
import com.fouristhenumber.utilitiesinexcess.utils.ColorUtils;
import com.gtnewhorizons.postea.api.IBlockTransformationHandler;
import com.gtnewhorizons.postea.utility.BlockConversionInfo;

public class ColoredBlocksTransformation implements IBlockTransformationHandler {

    private final int blockID;

    public ColoredBlocksTransformation(Block block) {
        this.blockID = Block.getIdFromBlock(block);
    }

    @Override
    public boolean apply(BlockConversionInfo info) {
        if (BlockColored.allowDyingBlocks()) {
            info.metadata = BlockColored.getEIDMetaFromRGB(ColorUtils.getHexColorFromWoolMeta(info.metadata));
        }
        info.blockID = this.blockID;
        return true;
    }
}
