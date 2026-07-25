package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityEnergyTransferNode;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.FluidWalker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class EnergyTransferNodeLogic extends NetworkLogic<TileEntityEnergyTransferNode> implements IInventory
{
    public FluidWalker walker;


    public EnergyTransferNodeLogic(TileEntityEnergyTransferNode host) {
        super(host);
    }

    @Override
    public boolean canConnectEnergy() {
        return false;
    }

    @Override
    public boolean canConnectFluid() {
        return false;
    }

    @Override
    public boolean canConnectItem() {
        return false;
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
