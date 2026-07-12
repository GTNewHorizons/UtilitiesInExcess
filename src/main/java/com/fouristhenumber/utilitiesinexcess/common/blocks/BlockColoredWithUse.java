package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockColoredWithUse extends BlockColored {

    public BlockColoredWithUse(Block base) {
        super(base);
    }

    public BlockColoredWithUse(Block base, float brightnessMultiplier) {
        super(base, brightnessMultiplier);
    }

    public BlockColoredWithUse(Block base, float brightnessMultiplier, int baseMeta) {
        super(base, brightnessMultiplier, baseMeta);
    }

    @Override
    public boolean usesExtraBit() {
        return true;
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        if (player.getHeldItem() != null) return false;

        int curMeta = worldIn.getBlockMetadata(x, y, z);
        boolean newExtraBit = !(getExtraMetaBit(curMeta) > 0);
        setExtraMetaBit(worldIn, x, y, z, curMeta, newExtraBit);
        if (!worldIn.isRemote) {
            playSoundEffect(worldIn, x, y, z, newExtraBit);
        }
        return true;
    }

    protected void playSoundEffect(World worldIn, int x, int y, int z, boolean newExtraBit) {
        worldIn.playSoundEffect(
            (double) x + 0.5D,
            (double) y + 0.5D,
            (double) z + 0.5D,
            "random.click",
            0.3F,
            newExtraBit ? 0.8F : 0.3F);
    }
}
