package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
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
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityItemTransferNode;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.ItemWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;
import com.fouristhenumber.utilitiesinexcess.utils.ItemStackInventory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class ItemTransferNodeLogic extends NetworkLogic<TileEntityItemTransferNode> implements IInventory
{
    ItemStack[] buffer = new ItemStack[getSizeInventory()];
    IInventory connectedInventory;
    public ItemWalker walker;

    public ItemTransferNodeLogic(TileEntityItemTransferNode host)
    {
        super(host);
        this.walker = new ItemWalker(host);
    }

    // Perhaps the best way to do this is creating a inserter logic that deals with insertion?
    // You can call walker.getInserter() then do inserter.insert()?

    // Note that I did write quite lengthy insertion logic for this. I felt that it is more important to keep the
    // logic concise and fast for the cases where there is no rationing pipe.
    public void updateEntity()
    {
        if (host.getWorld().isRemote || host.getWorld().getTotalWorldTime() % 20 != 0)
        {
            return;
        }

        if (connectedInventory == null) {
            updateSourceInventory();
        }

        if (connectedInventory != null) {
            importItems();
        }

        if (buffer[0] == null)
        {
            walker.reset();
            return;
        }

        List<TargetResolver.Target<IInventory>> targets = walker.getValidTargets(host.getWorld());
        if (!targets.isEmpty())
        {
            BaseInserter inserter = walker.getInserter(host.getWorld());
            for (TargetResolver.Target<IInventory> target : targets) // Need to loop through because sometimes the full stack cannot fit in one inventory
            {
                buffer[0] = inserter.insert(target, buffer[0]);
            }
        }
        walker.step(host.getWorld());
    }

    // Watch this one. I was lazy and had GPT re-write it to be sided.
    public void importItems()
    {
        ForgeDirection facing = host.getFacing();
        int[] slots;

        if (connectedInventory instanceof ISidedInventory sided)
        {
            slots = sided.getAccessibleSlotsFromSide(facing.ordinal());
        }
        else
        {
            int size = connectedInventory.getSizeInventory();
            slots = new int[size];
            for (int i = 0; i < size; i++)
            {
                slots[i] = i;
            }
        }

        for (int slot : slots)
        {
            ItemStack stackInSlot = connectedInventory.getStackInSlot(slot);
            if (stackInSlot == null)
            {
                continue;
            }

            // Respect sided extraction rules if applicable
            if (connectedInventory instanceof ISidedInventory sided)
            {
                if (!sided.canExtractItem(slot, stackInSlot, facing.ordinal()))
                {
                    continue;
                }
            }

            if (buffer[0] == null)
            {
                buffer[0] = stackInSlot.splitStack(1);

                if (stackInSlot.stackSize <= 0)
                {
                    connectedInventory.setInventorySlotContents(slot, null);
                }

                connectedInventory.markDirty();
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

                connectedInventory.markDirty();
                break;
            }
        }
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

    @Override
    public int getSizeInventory()
    {
        return 7;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn)
    {
        return buffer[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackInventory.decrStackSizeInItemStackArray(index, count, buffer, this);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index)
    {
        return buffer[index];
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        buffer[index] = stack;
        this.markDirty();
    }

    @Override
    public String getInventoryName()
    {
        return "gui.title.item_transfer_node.name";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
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

    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        StringSyncValue searchLocationSyncer = new StringSyncValue(() -> walker.getLocationString());
        syncManager.syncValue("searchLocationSyncer", searchLocationSyncer);

        SlotGroup bufferSlotGroup = new SlotGroup("transfer_node_buffer", 1);
        SlotGroup upgradeSlotGroup = new SlotGroup("transfer_node_upgrades", 1);

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

        panel.child(
            IKey.dynamic(() -> "Search Location: " + searchLocationSyncer.getStringValue())
                .asWidget()
                .marginTop(20)
                .horizontalCenter()
            );

        Flow flow = Flow.row();
        flow.pos(34,60).size(108,18);
        for (int i = 1; i < getSizeInventory(); i++) // First slot is for buffer
        {
            flow.child(new ItemSlot().slot(new ModularSlot(itemHandler,i).slotGroup(upgradeSlotGroup)));
        }
        panel.child(flow);
        ModularSlot slot = new ModularSlot(itemHandler, 0).slotGroup(bufferSlotGroup);

        panel.child(
            new Grid().coverChildren()
                .pos(79, 34)
                .mapTo(1, 1, index -> new ItemSlot().slot(slot)));

        return panel;
    }

    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(UtilitiesInExcess.MODID, mainPanel);
    }

    public void updateSourceInventory()
    {
        ForgeDirection facing = host.getFacing();
        TileEntity neighbor = host.getWorld().getTileEntity(host.getX() + facing.offsetX, host.getY() + facing.offsetY, host.getZ() + facing.offsetZ);
        if (neighbor instanceof IInventory inventory) {
            connectedInventory = inventory;
        }
    }
}
