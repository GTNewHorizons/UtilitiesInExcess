package com.fouristhenumber.utilitiesinexcess.utils;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;

public class RenderUtils {

    public static void renderInventoryCube(RenderBlocks renderer, Block block, int metadata) {
        renderer.renderFaceYNeg(block, 0, 0, 0, block.getIcon(0, metadata));
        renderer.renderFaceYPos(block, 0, 0, 0, block.getIcon(1, metadata));
        renderer.renderFaceZNeg(block, 0, 0, 0, block.getIcon(2, metadata));
        renderer.renderFaceZPos(block, 0, 0, 0, block.getIcon(3, metadata));
        renderer.renderFaceXNeg(block, 0, 0, 0, block.getIcon(4, metadata));
        renderer.renderFaceXPos(block, 0, 0, 0, block.getIcon(5, metadata));
    }
}
