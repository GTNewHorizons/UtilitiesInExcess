package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;

import java.util.ArrayList;

public class TileEntityTransferNode extends TileEntityTransferNodeBase
    implements ISidedInventory, IGuiHolder<PosGuiData> {

    ItemStack[] buffer=new ItemStack[getSizeInventory()];
    IInventory connectedInventory;

    @Override
    public boolean canConnectTo(World world, int x, int y, int z, int side) {
        if (side == blockMetadata) return false;

        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof ITransferNetworkComponent transfer) {
            return transfer.canConnectFrom(ForgeDirection.getOrientation(side));
        }

        if (te instanceof ISidedInventory sided) {
            int[] slots = sided.getAccessibleSlotsFromSide(side);
            return slots != null && slots.length > 0;
        } else return te instanceof IInventory;
    }

    public TileEntityTransferNode()
    {
        System.out.println("CALLED");
        Walker.setOriginal(this);
    }

    //int tick=0;
    @Override
    public void updateEntity() {
        //tick+=2+(2*0);
        //if (tick<=20) {return;}
        //tick=0;
        if (this.worldObj.isRemote || this.worldObj.getTotalWorldTime() % 20 != 0) return;
        if (buffer[0] != null && buffer[0].stackSize >= 64) return;
        if (connectedInventory == null) {
            ForgeDirection facing = ForgeDirection.getOrientation(getBlockMetadata());
            TileEntity neighbor = worldObj
                .getTileEntity(xCoord + facing.offsetX, yCoord + facing.offsetY, zCoord + facing.offsetZ);
            if (neighbor instanceof IInventory inventory) {
                connectedInventory = inventory;
            }
        }
        if (connectedInventory != null) importItems();


        Walker.step();

        TileEntity pipe=Walker.getCurrentTileEntity();

        ArrayList<TileEntity> ents=Walker.getItemEntities();

        for (TileEntity entity : ents)
        {
            boolean side=true;
            if (pipe instanceof TileEntityTransferNodeBase )
            {
                side=((TileEntityTransferNodeBase)pipe).canConnectFrom(Walker.getDirectionFromCurrent(entity));
            }
            if (side)
            {
                IInventory iEntity=(IInventory)entity;
                ArrayList<Integer> validSlots=new ArrayList<>(0);
                for (int i = 0; i < iEntity.getSizeInventory(); i++) {
                    ItemStack slot = iEntity.getStackInSlot(i);
                    if (buffer[0]==null || slot==null || (slot.isItemEqual(buffer[0]) && slot.getMaxStackSize()-slot.stackSize==0)) {continue;}
                    validSlots.add(i);


                }
                for (int i : validSlots)
                {
                    ItemStack slot = iEntity.getStackInSlot(i);
                    int remaining=slot.getMaxStackSize()-slot.stackSize;
                    if (buffer[0].stackSize <= remaining)
                    {
                        slot.stackSize+=buffer[0].stackSize;
                        buffer[0]=null;
                        iEntity.setInventorySlotContents(i,slot);
                        break;
                    }
                    slot.stackSize+=remaining;
                    buffer[0].stackSize-=remaining;
                    iEntity.setInventorySlotContents(i,slot);
                }
                for (int i = 0; i < iEntity.getSizeInventory(); i++) {
                    ItemStack slot = iEntity.getStackInSlot(i);
                    if (buffer[0]==null || (slot!=null && !buffer[0].isItemEqual(buffer[0]))) {continue;}
                    if (slot==null) { slot=new ItemStack(buffer[0].getItem(),0);}
                    slot.stackSize+=buffer[0].stackSize;
                    buffer[0]=null;
                    iEntity.setInventorySlotContents(i,slot);
                }


                if (true) //TODO: LINK WITH Round Robin
                {
                    Walker.reset();
                    break;
                }

            }
        }







    }

    public void importItems() {
        for (int slot = 0; slot < connectedInventory.getSizeInventory(); slot++) {
            ItemStack stackInSlot = connectedInventory.getStackInSlot(slot);
            if (stackInSlot != null) {
                if (buffer[0] == null) {
                    buffer[0] = stackInSlot.splitStack(1);
                    if (stackInSlot.stackSize <= 0) {
                        connectedInventory.setInventorySlotContents(slot, null);
                    }
                    break;
                } else if (buffer[0].isItemEqual(stackInSlot)) {
                    stackInSlot.splitStack(1);
                    buffer[0].stackSize += 1;
                    if (stackInSlot.stackSize <= 0) {
                        connectedInventory.setInventorySlotContents(slot, null);
                    }
                    break;
                }
            }
        }
    }
    //TODO: SWITCH ALL CODE TO BASENODE SO EVERY FILE HAS THIS SAVING AND UPGRADES
    @Override
    public void writeToNBT(NBTTagCompound shrug)
    {
        super.writeToNBT(shrug);
        for (int i = 0; i < getSizeInventory(); i++) {
            if (buffer[i]==null) {continue;}
            shrug.setTag(String.valueOf(i),buffer[i].writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound shrug)
    {
        super.readFromNBT(shrug);
        for (int i = 0; i < getSizeInventory(); i++) {
            if (!shrug.hasKey(String.valueOf(i))) {continue;}
            buffer[i]= ItemStack.loadItemStackFromNBT(shrug.getCompoundTag(String.valueOf(i)));
            this.setInventorySlotContents(i,buffer[i]);
        }
    }

    @Override
    public int getSizeInventory() { // 1(buffer[0])+6(upgrade slots)
        return 7;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return buffer[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (buffer[index] == null) return null;
        return buffer[index].splitStack(count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return buffer[index];
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        buffer[index] = stack;
        this.markDirty();
    }

    @Override
    public String getInventoryName() {
        return "gui.title.transfer_node.name";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {

        SlotGroup slotGroup = new SlotGroup("transfer_node_buffer[0]", 1);
        SlotGroup slotGroup1 = new SlotGroup("transfer_node_upgrades", 1);

        ModularPanel panel = new ModularPanel("panel");
        panel.bindPlayerInventory();

        panel.child(
            new ParentWidget<>().coverChildren()
                .topRelAnchor(0, 1)
                .child(
                    IKey.str(StatCollector.translateToLocal(getInventoryName()))
                        .asWidget()
                        .marginLeft(5)
                        .marginRight(5)
                        .marginTop(5)
                        .marginBottom(-15)));

        IItemHandler itemHandler = new InvWrapper(this);

        for (int i = 0; i < 6; i++) {
            panel.child(new ItemSlot().slot(new ModularSlot(itemHandler,i+1).slotGroup(slotGroup1)).pos(34+i*18,60));
        }
        ModularSlot slot = new ModularSlot(itemHandler, 0).slotGroup(slotGroup);

        panel.child(
            new Grid().coverChildren()
                .pos(79, 34)
                .mapTo(1, 1, index -> new ItemSlot().slot(slot)));

        return panel;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (ForgeDirection.getOrientation(side)
            .getOpposite()
            .ordinal() == blockMetadata) return null;
        return new int[] { 0 };
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return side != blockMetadata;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return false;
    }

}
