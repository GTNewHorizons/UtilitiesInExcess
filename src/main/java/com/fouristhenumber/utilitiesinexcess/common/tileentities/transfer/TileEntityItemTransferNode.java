package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import com.cleanroommc.modularui.screen.ModularScreen;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.IWalkingComponent;
import com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic.ItemTransferNodeLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;


public class TileEntityItemTransferNode extends TileEntityTransferNodeBase<ItemTransferNodeLogic>
    implements IGuiHolder<PosGuiData>, IWalkingComponent<ItemStack>
{

    public TileEntityItemTransferNode()
    {
        logic = new ItemTransferNodeLogic(this);
    }

    @Override
    public void updateEntity()
    {
        this.logic.updateEntity(this);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        this.logic.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.logic.readFromNBT(nbt);
    }

    @Override
    public int getSizeInventory()
    {
        return logic.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slotIn)
    {
        return logic.getStackInSlot(slotIn);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        return logic.decrStackSize(index, count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index)
    {
        return logic.getStackInSlotOnClosing(index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        logic.setInventorySlotContents(index, stack);
    }

    @Override
    public String getInventoryName()
    {
        return logic.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return logic.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return logic.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return logic.isUseableByPlayer(player);
    }

    @Override
    public void openInventory()
    {
    }

    @Override
    public void closeInventory()
    {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return logic.isItemValidForSlot(index, stack);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        return logic.buildUI(data, syncManager, settings, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return logic.createScreen(data, mainPanel);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        if (ForgeDirection.getOrientation(side).getOpposite().ordinal() == blockMetadata)
        {
            return null;
        }
        return new int[]
        {
            0
        };
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        return side != blockMetadata;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        return false;
    }

    @Override
    public void updateSourceInventory()
    {
        logic.updateSourceInventory(this);
    }

    @Override
    public ItemStack getWalkingObject() {
        return logic.getStackInSlot(0);
    }
}
