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
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityItemRetrievalNode;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.ItemWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;
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

import java.util.ArrayList;
import java.util.List;

import static com.fouristhenumber.utilitiesinexcess.transfer.walk.insertion.BaseInserter.canStacksMerge;

public class ItemRetrievalNodeLogic extends NetworkLogic<TileEntityItemRetrievalNode> implements IInventory
{
    public ItemWalker walker;
    ItemStack[] buffer = new ItemStack[getSizeInventory()];
    IInventory connectedInventory;

    private ItemStack pullingItem;

    public ItemRetrievalNodeLogic(TileEntityItemRetrievalNode host) {
        super(host);
        this.walker = new ItemWalker(host);
    }

    // Weird thing to note, retrieval node walkers just get locked out of filter pipes in all directions that are filtered.
    // All other types of pipes sans energy are fine to walk through.
    // Also, retrieval pipes will reset once one type of item has been emptied from the target inventory.
    // For example if you have 50 cobble and 10 dirt. It will take out all the dirt, reset move on, then take out all
    // the cobble on the next time it finds the target inventory.
    // Retrieval nodes can pull out of more than 1 inventory at a time. If two connected inventories are accessible
    // through the same block then
    public void updateEntity()
    {
        if (host.getWorld().isRemote || host.getWorld().getTotalWorldTime() % 20 != 0)
        {
            return;
        }

        if (connectedInventory == null)
        {
            updateConnectedInventory();
        }
        else
        {
            exportToConnected();
        }

        if (buffer[0] != null && buffer[0].stackSize == buffer[0].getMaxStackSize())
        {
            walker.reset();
            return;
        }

        List<TargetResolver.Target<IInventory>> pullingInventories = walker.getValidTargets(host.getWorld());

        if (pullingInventories.isEmpty())
        {
            walker.step(host.getWorld());
            return;
        }

        if (!importFromPullingInventories(pullingInventories))
        {
            pullingItem = null;
            walker.step(host.getWorld());
        }
    }

    public boolean importFromPullingInventories(List<TargetResolver.Target<IInventory>> pullingInventories)
    {
        boolean foundTargetItem = false;
        for (TargetResolver.Target<IInventory> target : pullingInventories)
        {
            foundTargetItem = importItem(target) | foundTargetItem;
        }
        return foundTargetItem;
    }

    public boolean importItem(TargetResolver.Target<IInventory> inv)
    {
        int[] slots;

        if (inv.handler instanceof ISidedInventory sidedHandler)
        {
            slots = sidedHandler.getAccessibleSlotsFromSide(inv.side);
        }
        else
        {
            slots = new int[inv.handler.getSizeInventory()];
            for (int i = 0; i < slots.length; i++)
            {
                slots[i] = i;
            }
        }

        for (int slot : slots)
        {
            ItemStack stack = inv.handler.getStackInSlot(slot);

            if (stack == null || stack.stackSize <= 0)
            {
                continue;
            }

            if (pullingItem == null)
            {
                // Sometimes if for some reason you have a transfer node that doesn't have a connected inventory
                // (or the inventory is full) you will just keep walking with a stack in the buffer.
                if (buffer[0] != null)
                {
                    pullingItem = buffer[0];
                }
                else
                {
                    pullingItem = stack.copy();
                    pullingItem.stackSize = 1;
                }
            }
            else if (!canStacksMerge(pullingItem, stack))
            {
                continue;
            }

            if (!addToOwnInventory(stack))
            {
                continue;
            }

            stack.stackSize--;
            inv.handler.setInventorySlotContents(slot, stack.stackSize <= 0 ? null : stack);

            return true;
        }

        return false;
    }

    private boolean addToOwnInventory(ItemStack sourceStack)
    {
        ItemStack existing = this.getStackInSlot(0);

        if (existing == null)
        {
            ItemStack single = sourceStack.copy();
            single.stackSize = 1;
            this.setInventorySlotContents(0, single);
            return true;
        }
        else if (canStacksMerge(existing, sourceStack) && existing.stackSize < existing.getMaxStackSize())
        {
            existing.stackSize++;
            this.setInventorySlotContents(0, existing);
            return true;
        }

        return false;
    }

    private void exportToConnected()
    {
        if (connectedInventory == null || buffer[0] == null || buffer[0].stackSize <= 0)
        {
            return;
        }

        ItemStack toExport = buffer[0];
        ForgeDirection connectedSide = host.getFacing();

        if (connectedInventory instanceof ISidedInventory sidedInventory)
        {
            int[] slots = sidedInventory.getAccessibleSlotsFromSide(connectedSide.ordinal());
            for (int slot : slots)
            {
                if (toExport.stackSize <= 0)
                {
                    break;
                }

                if (!sidedInventory.canInsertItem(slot, toExport, connectedSide.ordinal()))
                {
                    continue;
                }

                tryMergeIntoSlot(connectedInventory, slot, toExport);
            }
        }
        else
        {
            for (int slot = 0; slot < connectedInventory.getSizeInventory(); slot++)
            {
                if (toExport.stackSize <= 0)
                {
                    break;
                }

                if (!connectedInventory.isItemValidForSlot(slot, toExport))
                {
                    continue;
                }

                tryMergeIntoSlot(connectedInventory, slot, toExport);
            }
        }

        buffer[0] = toExport.stackSize <= 0 ? null : toExport;
    }

    private void tryMergeIntoSlot(IInventory inventory, int slot, ItemStack toExport)
    {
        ItemStack existing = inventory.getStackInSlot(slot);

        if (existing == null)
        {
            int moveCount = Math.min(toExport.stackSize, inventory.getInventoryStackLimit());
            ItemStack moved = toExport.copy();
            moved.stackSize = moveCount;
            inventory.setInventorySlotContents(slot, moved);
            toExport.stackSize -= moveCount;
        }
        else if (canStacksMerge(existing, toExport))
        {
            int space = Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit()) - existing.stackSize;

            if (space <= 0)
            {
                return;
            }

            int moveCount = Math.min(space, toExport.stackSize);
            existing.stackSize += moveCount;
            toExport.stackSize -= moveCount;
            inventory.setInventorySlotContents(slot, existing);
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

    public void updateConnectedInventory()
    {
        ForgeDirection facing = host.getFacing();
        TileEntity neighbor = host.getWorld().getTileEntity(host.getX() + facing.offsetX, host.getY() + facing.offsetY, host.getZ() + facing.offsetZ);
        if (neighbor instanceof IInventory inventory) {
            connectedInventory = inventory;
        }
    }

    @Override
    public int getSizeInventory() {
        return 7;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return buffer[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (buffer[index] == null)
        {
            return null;
        }
        return buffer[index].splitStack(count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return buffer[index];
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.buffer[index] = stack;
        this.markDirty();
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
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        StringSyncValue searchLocationSyncer = new StringSyncValue(() -> walker.getLocationString());
        syncManager.syncValue("searchLocationSyncer", searchLocationSyncer);

        SlotGroup bufferSlotGroup = new SlotGroup("retrieval_node_buffer", 1);
        SlotGroup upgradeSlotGroup = new SlotGroup("retrieval_node_upgrades", 1);

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
}
