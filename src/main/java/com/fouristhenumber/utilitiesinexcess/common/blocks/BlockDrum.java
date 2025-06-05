package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityDrum;

public class BlockDrum extends BlockContainer {

    int capacity = 16000;

    public BlockDrum() {
        super(Material.iron);
        setBlockName("drum");
        setBlockTextureName("utilitiesinexcess:drum");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityDrum(capacity);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    public static class ItemBlockDrum extends ItemBlock {

        int capacity;

        public ItemBlockDrum(Block block) {
            super(block);
            this.capacity = ((BlockDrum) block).capacity;
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip.add(
                StatCollector
                    .translateToLocalFormatted("tile.drum.desc", (int) Math.floor(((float) capacity) / 1000f)));
        }
    }

}
