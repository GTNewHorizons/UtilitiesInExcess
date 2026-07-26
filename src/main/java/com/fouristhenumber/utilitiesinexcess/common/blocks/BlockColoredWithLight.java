package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockColoredWithLight extends BlockColoredWithUse {

    public BlockColoredWithLight(Block base) {
        super(base);
    }

    public BlockColoredWithLight(Block base, float brightnessMultiplier) {
        super(base, brightnessMultiplier);
    }

    public BlockColoredWithLight(Block base, float brightnessMultiplier, int baseMeta) {
        super(base, brightnessMultiplier, baseMeta);
    }

    public BlockColoredWithLight(String baseModID, String baseName, int baseMeta, float brightnessMultiplier) {
        super(baseModID, baseName, baseMeta, brightnessMultiplier);
    }

    @Override
    protected void playSoundEffect(World worldIn, int x, int y, int z, boolean newExtraBit) {
        worldIn.playSoundEffect(
            (double) x + 0.5D,
            (double) y + 0.5D,
            (double) z + 0.5D,
            "random.click",
            0.3F,
            newExtraBit ? 0.3F : 0.8F);
    }

    @Override
    public String getCustomNEIPage() {
        return "uie.nei.infopage.colored_blocks.light";
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return BlockColored.getExtraMetaBit(world.getBlockMetadata(x, y, z)) != 0 ? 0 : 10;
    }

    @Override
    public int getLightValue() {
        return 10;
    }

    public static class ItemBlockColoredWithLight extends ItemBlockColored {

        public ItemBlockColoredWithLight(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean p_77624_4_) {
            super.addInformation(stack, player, tooltip, p_77624_4_);
            if (BlockColored.getExtraMetaBit(stack.getItemDamage()) > 0) {
                tooltip.add(StatCollector.translateToLocal("uie.desc.block_colored_light"));
            }
        }
    }
}
