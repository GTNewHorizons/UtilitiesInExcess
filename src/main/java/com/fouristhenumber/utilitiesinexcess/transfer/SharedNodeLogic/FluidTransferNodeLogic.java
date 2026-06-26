package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;

import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityFluidTransferNode;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.FluidWalker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class FluidTransferNodeLogic extends NetworkLogic<TileEntityFluidTransferNode> implements IInventory, IFluidHandler
{
    ItemStack[] upgrades = new ItemStack[getSizeInventory()];

    FluidStack buffer;

    IFluidHandler connectedTank;
    public FluidWalker walker;

    public FluidTransferNodeLogic(TileEntityFluidTransferNode host ) {
        super(host);
        walker = new FluidWalker(host);
    }

    public void updateEntity(INodeLogicHost host)
    {
        if (host.getWorld().isRemote || host.getWorld().getTotalWorldTime() % 20 != 0)
        {
            return;
        }

        if (connectedTank == null)
        {
            updateSourceTank();
        }

        if (connectedTank != null)
        {
            importFluids();
        }
    }

    public void importFluids()
    {

    }

    @Override
    public boolean canConnectEnergy() {
        return true;
    }

    @Override
    public boolean canConnectFluid() {
        return true;
    }

    @Override
    public boolean canConnectItem() {
        return true;
    }

    @Override
    public int getSizeInventory() {
        return 5;
    }

    public void updateSourceTank()
    {
        ForgeDirection facing = host.getFacing();
        TileEntity neighbor = host.getWorld().getTileEntity(host.getX() + facing.offsetX, host.getY() + facing.offsetY, host.getZ() + facing.offsetZ);
        if (neighbor instanceof IFluidHandler tank)
        {
            connectedTank = tank;
        }
    }

    @Override
    public ItemStack getStackInSlot(int slotIn)
    {
        return upgrades[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        if (upgrades[index] == null)
        {
            return null;
        }
        return upgrades[index].splitStack(count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index)
    {
        return upgrades[index];
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        upgrades[index] = stack;
        this.markDirty();
    }

    @Override
    public String getInventoryName()
    {
        return "gui.title.fluid_transfer_node.name";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void markDirty() {
        host.markHostDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
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

    // Fluid logic
    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[0];
    }
}
