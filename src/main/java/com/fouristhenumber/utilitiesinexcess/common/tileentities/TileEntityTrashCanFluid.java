package com.fouristhenumber.utilitiesinexcess.common.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;

public class TileEntityTrashCanFluid extends TileEntity
    implements IGuiHolder<PosGuiData>, ISidedInventory, IFluidHandler {

    public final ItemStack[] mInventory;
    private final ItemStackHandler inventoryHandler;

    public final int INPUT_SLOT = 0;
    public final int OUTPUT_SLOT = 1;

    public TileEntityTrashCanFluid() {
        mInventory = new ItemStack[] { null, null };
        inventoryHandler = new ItemStackHandler(mInventory) {

            @Override
            protected void onContentsChanged(int slot) {
                ItemStack inputStack = this.getStackInSlot(INPUT_SLOT);
                if (inputStack == null || !isValidItemInput(inputStack)) return;

                ItemStack drainedStack = FluidContainerRegistry.drainFluidContainer(inputStack);
                if (drainedStack == null) return;

                ItemStack outputStack = this.getStackInSlot(OUTPUT_SLOT);
                if (outputStack != null && !outputStack.getItem()
                    .equals(drainedStack.getItem())) return;
                // We interface directly with the inventory here, since interfacing with the inventoryHandler
                // would call onContentsChanged again.
                int inserted;
                if (outputStack == null) {
                    inserted = inputStack.stackSize;
                    drainedStack.stackSize = inserted;
                    mInventory[OUTPUT_SLOT] = drainedStack;
                } else {
                    // Make sure we don't insert more than stackLimit
                    // aka, outputStack.stackSize += stackLimit - outputStack.stackSize <= stackLimit
                    int stackLimit = drainedStack.getMaxStackSize();
                    inserted = Math.min(stackLimit - outputStack.stackSize, inputStack.stackSize);
                    outputStack.stackSize += inserted;
                }
                inputStack.stackSize -= inserted;
                if (inputStack.stackSize <= 0) {
                    mInventory[INPUT_SLOT] = null;
                }
            }
        };
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {

        syncManager.registerSlotGroup("item_inv", 0);

        ModularPanel panel = new ModularPanel("panel");
        panel.bindPlayerInventory();

        // Add title
        panel.child(
            new ParentWidget<>().coverChildren()
                .topRelAnchor(0, 1)
                .child(
                    IKey.str(StatCollector.translateToLocal("tile.trashCanFluid.name"))
                        .asWidget()
                        .marginLeft(5)
                        .marginRight(5)
                        .marginTop(5)
                        .marginBottom(-15)));

        IWidget slots = Flow.column()
            .childPadding(10)
            .coverChildren()
            .child(
                new ItemSlot().slot(
                    new ModularSlot(inventoryHandler, INPUT_SLOT).slotGroup("item_inv")
                        .filter(TileEntityTrashCanFluid::isValidItemInput)))
            .pos(79, 25)
            .child(
                new ItemSlot().slot(
                    new ModularSlot(inventoryHandler, OUTPUT_SLOT).slotGroup("item_inv")
                        .accessibility(false, true)));

        return panel.child(slots);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStack stackIn = mInventory[INPUT_SLOT];
        if (stackIn != null) {
            NBTTagCompound slotIn = new NBTTagCompound();
            stackIn.writeToNBT(slotIn);
            compound.setTag("slotIn", slotIn);
        }

        ItemStack stackOut = mInventory[OUTPUT_SLOT];
        if (stackOut != null) {
            NBTTagCompound slotOut = new NBTTagCompound();
            stackOut.writeToNBT(slotOut);
            compound.setTag("slotOut", slotOut);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        UtilitiesInExcess.LOG.info(compound);
        super.readFromNBT(compound);
        if (compound.hasKey("slotIn")) {
            ItemStack slotIn = new ItemStack(Items.feather, 1);
            slotIn.readFromNBT(compound.getCompoundTag("slotIn"));
            mInventory[INPUT_SLOT] = slotIn;
        }
        if (compound.hasKey("slotOut")) {
            ItemStack slotOut = new ItemStack(Items.feather, 1);
            slotOut.readFromNBT(compound.getCompoundTag("slotOut"));
            mInventory[OUTPUT_SLOT] = slotOut;
        }
    }

    // Item Handler Methods
    @Override
    public int getSizeInventory() {
        return inventoryHandler.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventoryHandler.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return inventoryHandler.extractItem(index, count, false);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventoryHandler.setStackInSlot(index, stack);
    }

    @Override
    public String getInventoryName() {
        return StatCollector.translateToLocal("tile.trashCanFluid.name");
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
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
        if (index == INPUT_SLOT) return isValidItemInput(stack);
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        return new int[] { INPUT_SLOT, OUTPUT_SLOT };
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack item, int side) {
        return slot == INPUT_SLOT && isValidItemInput(item)
            && !(item.equals(inventoryHandler.insertItem(slot, item, true)));
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack item, int side) {
        if (slot != OUTPUT_SLOT) return false;
        if (item == null) return false;
        if (inventoryHandler.getStackInSlot(slot)
            .getItem() != item.getItem()) return false;
        ItemStack extractedStack = inventoryHandler.extractItem(slot, item.stackSize, true);
        return extractedStack != null && extractedStack.stackSize > 0;
    }

    // Fluid Handler Methods
    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        int amount = resource.amount;
        if (doFill) resource.amount = 0;
        return amount;
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
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { new FluidTankInfo(null, Integer.MAX_VALUE) };
    }

    public static boolean isValidItemInput(ItemStack itemStack) {
        return FluidContainerRegistry.isFilledContainer(itemStack);
    }

}
