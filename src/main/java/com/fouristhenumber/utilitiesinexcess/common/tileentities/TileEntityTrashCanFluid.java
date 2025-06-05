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

                ItemStack unInserted = this.insertItem(OUTPUT_SLOT, drainedStack, false);

                if (unInserted == null) {
                    this.setStackInSlot(0, null);
                } else {
                    inputStack.stackSize -= (drainedStack.stackSize - unInserted.stackSize);
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

        IWidget waterSlots = Flow.column()
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

        return panel.child(waterSlots);
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
        return mInventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return mInventory[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (mInventory[index] == null) return null;
        ItemStack returnStack = new ItemStack(mInventory[index].getItem(), count, mInventory[index].getItemDamage());
        mInventory[index].stackSize -= count;
        return returnStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        mInventory[index] = stack;
    }

    @Override
    public String getInventoryName() {
        return "Trash Can (Fluid)";
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
    public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
        return p_102007_1_ == INPUT_SLOT && isValidItemInput(p_102007_2_);
    }

    @Override
    public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
        if (p_102008_1_ != OUTPUT_SLOT) return false;
        ItemStack outputStack = mInventory[OUTPUT_SLOT];
        if (outputStack == null || p_102008_2_ == null
            || outputStack.getItem()
                .equals(p_102008_2_.getItem()))
            return false;

        return true;
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
        return FluidContainerRegistry.isContainer(itemStack);
    }

}
