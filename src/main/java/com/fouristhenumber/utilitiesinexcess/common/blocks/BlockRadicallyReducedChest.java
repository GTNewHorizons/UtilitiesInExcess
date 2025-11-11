package com.fouristhenumber.utilitiesinexcess.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityRadicallyReducedChest;

public class BlockRadicallyReducedChest extends BlockSignificantlyShrunkChest {

    public BlockRadicallyReducedChest() {
        super();
        setBlockName("radically_reduced_chest");
        setBlockBounds(0.4f, 0, 0.4f, 0.6f, 0.2f, 0.6f);
        setHardness(1.5F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityRadicallyReducedChest();
    }

    public static class ItemBlockRadicallyReducedChest extends ItemBlock {

        public ItemBlockRadicallyReducedChest(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip.add(StatCollector.translateToLocalFormatted("tile.radically_reduced_chest.desc"));
        }
    }
}
