package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityFluidRetrievalNode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ItemRetrievalNodeLogic extends NetworkLogic<TileEntityFluidRetrievalNode> implements IInventory
{
    public ItemRetrievalNodeLogic(TileEntityFluidRetrievalNode host) {
        super(host);
    }

    // Weird thing to note, retrieval node walkers just get locked out of filter pipes in all directions that are filtered.
    // Also, retrieval pipes will reset once one type of item has been emptied from the target inventory.
    // For example if you have 50 cobble and 10 dirt. It will take out all the dirt, reset, then take out all
    // the cobble on the next time it finds the target inventory.
    public void updateEntity()
    {

    }

    @Override
    public int getSizeInventory() {
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

    }

    @Override
    public String getInventoryName() {
        return "";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 0;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }
}
