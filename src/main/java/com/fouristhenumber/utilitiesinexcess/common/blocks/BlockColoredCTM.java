package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;

import com.prupe.mcpatcher.ctm.ICTMBlock;
import com.prupe.mcpatcher.ctm.RenderBlockState;

public class BlockColoredCTM extends BlockColoredWithUse implements ICTMBlock {

    public static final ArrayList<BlockColored> CTM_COLORED_BLOCKS = new ArrayList<>();

    public BlockColoredCTM(Block base) {
        super(base);
        CTM_COLORED_BLOCKS.add(this);
    }

    public BlockColoredCTM(Block base, float brightnessMultiplier) {
        super(base, brightnessMultiplier);
        CTM_COLORED_BLOCKS.add(this);
    }

    public BlockColoredCTM(Block base, float brightnessMultiplier, int baseMeta) {
        super(base, brightnessMultiplier, baseMeta);
        CTM_COLORED_BLOCKS.add(this);
    }

    @Override
    public boolean shouldConnectByBlock(RenderBlockState renderBlockState, Block neighbor, int neighborX, int neighborY,
        int neighborZ) {
        if (renderBlockState.getBlock() != neighbor) {
            return false;
        }

        int neighborMeta = renderBlockState.getBlockAccess()
            .getBlockMetadata(neighborX, neighborY, neighborZ);
        boolean extraBit1 = BlockColored.getExtraMetaBit(renderBlockState.getMetadata()) != 0;
        boolean extraBit2 = BlockColored.getExtraMetaBit(neighborMeta) != 0;
        return (renderBlockState.getMetadata() & 0b0_11111_11111_11111) == (neighborMeta & 0b0_11111_11111_11111)
            || (extraBit1 && extraBit2);
    }
}
