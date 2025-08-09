package com.fouristhenumber.utilitiesinexcess.common.blocks;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTradingPost;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTrashCanItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class BlockTradingPost extends BlockContainer {

    public BlockTradingPost() {
        super(Material.wood);
        setBlockName("trading_post");
        setBlockTextureName("utilitiesinexcess:trading_post");
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer playerIn, int side, float hitX,
        float hitY, float hitZ) {
        if(worldIn.isRemote)
        {
            TileEntityTradingPost te=(TileEntityTradingPost)worldIn.getTileEntity(x,y,z);
            te.updateCachedTrades(playerIn);
        }
        if (!worldIn.isRemote) {

            GuiFactories.tileEntity()
                .open(playerIn, x, y, z);
        }
        return true;
    }

    //Does this need to be a tile entity?
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTradingPost();
    }

    public static class ItemBlockTradingPost extends ItemBlock {

        public ItemBlockTradingPost(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip.add(StatCollector.translateToLocal("tile.trading_post.desc"));
        }
    }
}
