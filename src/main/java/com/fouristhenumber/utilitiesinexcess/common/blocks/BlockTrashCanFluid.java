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
import net.minecraftforge.fluids.FluidContainerRegistry;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTrashCanFluid;

public class BlockTrashCanFluid extends BlockContainer {

    public BlockTrashCanFluid() {
        super(Material.rock);
        setBlockName("trashCanFluid");
        setBlockTextureName("utilitiesinexcess:trash_can_fluid");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        ItemStack heldItem = player.getCurrentEquippedItem();
        if (heldItem == null) {
            GuiFactories.tileEntity()
                .open(player, x, y, z);
            return true;
        }

        if (FluidContainerRegistry.isFilledContainer(heldItem)) {
            ItemStack emptyContainer = FluidContainerRegistry.drainFluidContainer(heldItem);
            emptyContainer.stackSize = heldItem.stackSize;
            player.inventory.setInventorySlotContents(player.inventory.currentItem, emptyContainer);
            return true;
        } else {
            GuiFactories.tileEntity()
                .open(player, x, y, z);
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTrashCanFluid();
    }

    public static class ItemBlockTrashCanFluid extends ItemBlock {

        public ItemBlockTrashCanFluid(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean bool) {
            tooltip.add(StatCollector.translateToLocal("tile.trashCanFluid.desc"));
        }
    }
}
