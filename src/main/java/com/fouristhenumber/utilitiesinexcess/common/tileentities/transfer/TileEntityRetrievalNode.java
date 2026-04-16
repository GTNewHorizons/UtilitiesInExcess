package com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer;

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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;

public class TileEntityRetrievalNode extends TileEntityTransferNode
    implements ISidedInventory, IGuiHolder<PosGuiData> {

    ItemStack buffer;
    IInventory connectedInventory;

    public TileEntityRetrievalNode()
    {
        System.out.println("CALLED");
        Walker.setOriginal(this);
    }

    boolean foundEntity=false;
    @Override
    public void updateEntity() {
        //tick+=2+(2*0); //0 is speedupgrade
        //if (tick<=20) {return;}
        //tick=0;
        if (this.worldObj.isRemote || this.worldObj.getTotalWorldTime() % 20 != 0) return;
        if (buffer != null && buffer.stackSize >= 64) return;
        if (connectedInventory == null) {
            ForgeDirection facing = ForgeDirection.getOrientation(getBlockMetadata());
            TileEntity neighbor = worldObj
                .getTileEntity(xCoord + facing.offsetX, yCoord + facing.offsetY, zCoord + facing.offsetZ);
            if (neighbor instanceof IInventory inventory) {
                connectedInventory = inventory;
            }
        }

        if (connectedInventory != null) exportItems();

        if (!foundEntity)
        {
            Walker.step();
        }
        TileEntity pipe=Walker.getCurrentTileEntity();
        ArrayList<TileEntity> ents=Walker.getItemEntities();
        foundEntity=false;
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
                    if (slot==null || (buffer!=null && !buffer.isItemEqual(slot))) {continue;}
                    if (buffer==null) { buffer=new ItemStack(slot.getItem(),0);}
                    foundEntity=true;
                    slot.stackSize-=1;
                    buffer.stackSize+=1;
                    this.setInventorySlotContents(0,buffer);
                    if (slot.stackSize==0)
                    {
                        slot=null;
                        iEntity.setInventorySlotContents(i,null);
                    }
                    break;
                }

                if (true) //TODO: LINK WITH Round Robin
                {
                    if (!foundEntity)
                    {
                        Walker.reset();
                    }
                } else
                {
                    break;
                }


            }
        }


    }
    // TODO: Swap to ExportItems 🤤 - Should be done now
    public void exportItems() {
        ArrayList<Integer> validSlots=new ArrayList<>(0);
        for (int i = 0; i < connectedInventory.getSizeInventory(); i++) {
            ItemStack slot = connectedInventory.getStackInSlot(i);
            if (buffer==null || slot==null || (slot.isItemEqual(buffer) && slot.getMaxStackSize()-slot.stackSize==0)) {continue;}
            validSlots.add(i);
        }
        for (int i = 0; i < connectedInventory.getSizeInventory(); i++) { // could compress into another array and do .addall
            ItemStack slot = connectedInventory.getStackInSlot(i);
            if (slot!=null) {continue;}
            validSlots.add(i);
        }

        for (int slot: validSlots) {
            ItemStack stackInSlot = connectedInventory.getStackInSlot(slot);
            if (buffer!=null) {
                if (stackInSlot == null) {
                    stackInSlot = buffer.splitStack(1);
                    if (buffer.stackSize <= 0) {
                        buffer=null;
                    }
                    connectedInventory.setInventorySlotContents(slot,stackInSlot);
                    break;
                } else if (buffer.isItemEqual(stackInSlot)) {
                    //if (stackInSlot.stackSize==stackInSlot.getMaxStackSize()) {continue;}
                    buffer.splitStack(1);
                    stackInSlot.stackSize += 1;
                    if (buffer.stackSize <= 0) {
                        buffer=null;
                        this.setInventorySlotContents(0,null);
                    }
                    connectedInventory.setInventorySlotContents(slot,stackInSlot);
                    break;
                }
            }
        }
    }

    @Override
    public String getInventoryName() {
        return "gui.title.transfer_node.name";
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return side != blockMetadata;
    }

}
