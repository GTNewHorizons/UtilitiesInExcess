package com.fouristhenumber.utilitiesinexcess.common.blocks;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntitySoundMuffler;
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

public class BlockSoundMuffler extends BlockContainer {

    public BlockSoundMuffler() {
        super(Material.sponge);
        setStepSound(soundTypeCloth);
        setBlockName("sound_muffler");
        setBlockTextureName("utilitiesinexcess:sound_muffler");
        setHardness(0.5f);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySoundMuffler();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntitySoundMuffler) {
            ((TileEntitySoundMuffler) te).onInputChanged();
        }
        super.onNeighborBlockChange(world, x, y, z, neighbor);
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof TileEntitySoundMuffler) {
            ((TileEntitySoundMuffler) te).disableMuffler();
        }
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    public static class ItemBlockSoundMuffler extends ItemBlock {

        public ItemBlockSoundMuffler(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            String formatted = StatCollector.translateToLocalFormatted(
                "tile.sound_muffler.desc.1",
                BlockConfig.soundMuffler.soundMufflerRange,
                BlockConfig.soundMuffler.soundMufflerReduction);
            tooltip.add(formatted);
            tooltip.add(StatCollector.translateToLocal("tile.sound_muffler.desc.2"));
        }
    }
}
