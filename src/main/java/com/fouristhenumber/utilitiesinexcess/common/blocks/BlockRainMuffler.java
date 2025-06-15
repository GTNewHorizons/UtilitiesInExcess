package com.fouristhenumber.utilitiesinexcess.common.blocks;


import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRainMuffler;
import com.fouristhenumber.utilitiesinexcess.config.BlockConfig;
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

public class BlockRainMuffler extends BlockContainer {
    public BlockRainMuffler() {
        super(Material.sponge);
        setStepSound(soundTypeCloth);
        setBlockName("rain_muffler");
        setBlockTextureName("utilitiesinexcess:rain_muffler");
        setHardness(0.5f);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {

        return new TileEntityRainMuffler();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityRainMuffler muffler) {
            muffler.onInputChanged();
        }
        super.onNeighborBlockChange(world, x, y, z, neighbor);
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof TileEntityRainMuffler muffler) {
            muffler.onInputChanged();
        }
        super.onBlockAdded(worldIn, x, y, z);
    }

    public static class ItemBlockRainMuffler extends ItemBlock {

        public ItemBlockRainMuffler(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            String formatted = StatCollector.translateToLocalFormatted(
                "tile.rain_muffler.desc.1",
                BlockConfig.rainMuffler.rainMufflerRange);
            tooltip.add(formatted);
            tooltip.add(StatCollector.translateToLocal("tile.rain_muffler.desc.2"));
            tooltip.add(StatCollector.translateToLocal("tile.rain_muffler.desc.3"));
        }
    }
}
