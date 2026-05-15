package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemTransferNodeLogic extends NetworkLogic
{

    ItemStack[] buffer = new ItemStack[getSizeInventory()];

    IInventory connectedInventory;

    public ItemTransferNodeLogic(INodeLogicHost host)
    {
        super(host);
    }

    public void updateEntity(INodeLogicHost host) {
        if (host.getWorld().isRemote || host.getWorld().getTotalWorldTime() % 20 != 0) {
            return;
        }

        if (connectedInventory == null) {
            ForgeDirection facing = host.getFacing();
            TileEntity neighbor = host.getWorld().getTileEntity(host.getX() + facing.offsetX, host.getY() + facing.offsetY, host.getZ() + facing.offsetZ);
            if (neighbor instanceof IInventory inventory) {
                connectedInventory = inventory;
            }
        }

        if (connectedInventory != null) {
            importItems();
        }

        if (buffer[0] == null)
        {
            walker.reset();
            return;
        }
        walker.step();

        walker.getValidTarget();
        if ()
        {

        }
    }

    public void importItems() {
        for (int slot = 0; slot < connectedInventory.getSizeInventory(); slot++)
        {
            ItemStack stackInSlot = connectedInventory.getStackInSlot(slot);
            if (stackInSlot != null)
            {
                if (buffer[0] == null)
                {
                    buffer[0] = stackInSlot.splitStack(1);
                    if (stackInSlot.stackSize <= 0)
                    {
                        connectedInventory.setInventorySlotContents(slot, null);
                    }
                    break;
                }
                else if (buffer[0].isItemEqual(stackInSlot))
                {
                    stackInSlot.splitStack(1);
                    buffer[0].stackSize += 1;
                    if (stackInSlot.stackSize <= 0)
                    {
                        connectedInventory.setInventorySlotContents(slot, null);
                    }
                    break;
                }
            }
        }
    }

    public void exportItems()
    {

    }


    public void writeToNBT(NBTTagCompound nbt)
    {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.buffer.length; ++i)
        {
            if (this.buffer[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.buffer[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        nbt.setTag("Items", nbttaglist);
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        NBTTagList nbttaglist = nbt.getTagList("Items", 10);
        this.buffer = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound compound = nbttaglist.getCompoundTagAt(i);
            int slot = compound.getByte("Slot") & 255;

            if (slot < this.buffer.length)
            {
                this.buffer[slot] = ItemStack.loadItemStackFromNBT(compound);
            }
        }
    }

    public int getSizeInventory()
    {
        return 7;
    }

    public ItemStack getStackInSlot(int slotIn)
    {
        return buffer[slotIn];
    }

    public ItemStack decrStackSize(int index, int count)
    {
        if (buffer[index] == null)
        {
            return null;
        }
        return buffer[index].splitStack(count);
    }

    public ItemStack getStackInSlotOnClosing(int index)
    {
        return buffer[index];
    }

    public void setInventorySlotContents(int index, ItemStack stack, INodeLogicHost host)
    {
        buffer[index] = stack;
        host.markDirty();
    }

    public String getInventoryName()
    {
        return "gui.title.transfer_node.name";
    }

    public boolean hasCustomInventoryName()
    {
        return false;
    }

    public int getInventoryStackLimit()
    {
        return 64;
    }

    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    public void openInventory()
    {}

    public void closeInventory()
    {}

    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings, INodeLogicHost host)
    {
        StringSyncValue searchLocationSyncer = new StringSyncValue(() -> walker.getLocationString());
        syncManager.syncValue("searchLocationSyncer", searchLocationSyncer);

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

        IItemHandler itemHandler = new InvWrapper(host);

        panel.child(
            IKey.dynamic(() -> "Search Location: " + searchLocationSyncer.getStringValue())
                .asWidget()
                .marginTop(20)
                .horizontalCenter()
            );

        Flow flow = Flow.row();
        flow.pos(34,60).size(108,18);
        for (int i = 0; i < 6; i++)
        {
            flow.child(new ItemSlot().slot(new ModularSlot(itemHandler,i + 1).slotGroup(slotGroup1)));
        }
        panel.child(flow);
        ModularSlot slot = new ModularSlot(itemHandler, 0).slotGroup(slotGroup);

        panel.child(
            new Grid().coverChildren()
                .pos(79, 34)
                .mapTo(1, 1, index -> new ItemSlot().slot(slot)));

        return panel;
    }
}
