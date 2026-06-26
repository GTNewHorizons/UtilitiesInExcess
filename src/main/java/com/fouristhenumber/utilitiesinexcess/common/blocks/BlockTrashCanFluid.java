package com.fouristhenumber.utilitiesinexcess.common.blocks;

import static com.fouristhenumber.utilitiesinexcess.utils.UIEUtils.dropItemsFromIInventory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.TileEntityTrashCanFluid;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

public class BlockTrashCanFluid extends BlockContainer {

    public BlockTrashCanFluid() {
        super(Material.rock);
        setHardness(3.5F);
        setBlockName("trash_can_fluid");
        setBlockTextureName("utilitiesinexcess:trash_can_fluid");
        setBlockBounds(0F, 0.0F, 0F, 1F, 0.875F, 1F);
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
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te instanceof IInventory inv) {
            dropItemsFromIInventory(0, inv, worldIn, te.xCoord, te.yCoord, te.zCoord);
        }
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTrashCanFluid();
    }

    @Override
    public int getRenderType() {
        return ModelISBRH.JSON_ISBRH_ID;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
