package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityPureLove;
import com.fouristhenumber.utilitiesinexcess.config.BlockConfig;

public class BlockPureLove extends BlockContainer {

    public BlockPureLove() {
        super(Material.iron);
        setStepSound(soundTypeMetal);
        setHardness(1f);
        setBlockName("pure_love");
        setBlockTextureName("utilitiesinexcess:pure_love");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityPureLove();
    }

    public static class ItemBlockPureLove extends ItemBlock {

        public ItemBlockPureLove(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip.add(
                EnumChatFormatting.LIGHT_PURPLE + ""
                    + EnumChatFormatting.ITALIC
                    + StatCollector
                        .translateToLocalFormatted("tile.pure_love.desc", BlockConfig.pureLove.rangePureLove));
        }
    }
}
