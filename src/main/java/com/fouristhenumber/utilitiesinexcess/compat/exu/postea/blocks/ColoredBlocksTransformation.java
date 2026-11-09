package com.fouristhenumber.utilitiesinexcess.compat.exu.postea.blocks;

import net.minecraft.block.Block;

import com.fouristhenumber.utilitiesinexcess.common.blocks.BlockColored;
import com.fouristhenumber.utilitiesinexcess.utils.ColorUtils;
import com.gtnewhorizons.postea.api.BlockReplacementManager;
import com.gtnewhorizons.postea.api.IBlockTransformationHandler;
import com.gtnewhorizons.postea.utility.BlockConversionInfo;

public class ColoredBlocksTransformation implements IBlockTransformationHandler {

    private int blockID = -1;

    public ColoredBlocksTransformation(Block block) {
        String registryName = Block.blockRegistry.getNameForObject(block);
        BlockReplacementManager.registerIDResolver(registryName, id -> this.blockID = id);
    }

    @Override
    public boolean apply(BlockConversionInfo info) {
        if (this.blockID < 0) return false;

        if (BlockColored.allowDyingBlocks()) {
            info.metadata = BlockColored.getEIDMetaFromRGB(ColorUtils.getHexColorFromWoolMeta(info.metadata));
        }
        info.blockID = this.blockID;
        return true;
    }
}
