package com.fouristhenumber.utilitiesinexcess.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockColoredRotatable extends BlockColoredWithUse {

    public BlockColoredRotatable(Block base) {
        super(base);
    }

    public BlockColoredRotatable(Block base, float brightnessMultiplier) {
        super(base, brightnessMultiplier);
    }

    public BlockColoredRotatable(Block base, float brightnessMultiplier, int baseMeta) {
        super(base, brightnessMultiplier, baseMeta);
    }

    public BlockColoredRotatable(String baseModID, String baseName, int baseMeta, float brightnessMultiplier) {
        super(baseModID, baseName, baseMeta, brightnessMultiplier);
    }

    @Override
    public String getCustomNEIPage() {
        return "uie.nei.infopage.colored_blocks.rotatable";
    }

    public IIcon[] iconsRotated = new IIcon[6];

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (iconsRotated != null && BlockColored.getExtraMetaBit(meta) != 0) {
            return iconsRotated[side];
        }

        return super.getIcon(side, meta);
    }
}
