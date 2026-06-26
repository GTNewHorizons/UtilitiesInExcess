package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;

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
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.TargetResolver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.IntArrayList;
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
        walker = new ItemWalker(host);
    }

    // Note that I did write quite lengthy insertion logic for this. I felt that it is more important to keep the
    // logic consise and fast for the cases where there is no rationing pipe.
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

        List<TargetResolver.Target<IInventory>> targets = walker.getValidTargets();
        if (!targets.isEmpty())
        {
            // As mentioned elsewhere some pipes have a maximum insertion limit.
            int insertLimit = walker.getInsertLimit();
            if (insertLimit == -1) // Unlimited insert logic
            {
                for (TargetResolver.Target<IInventory> target : targets) // Need to loop through because sometimes the full stack cannot fit in one inventory
                {
                    if (target.handler instanceof ISidedInventory sidedInventory) // Sided logic
                    {
                        buffer[0] = TryInsertItemSided(sidedInventory, buffer[0], target.side);
                    }
                    else // Basic logic
                    {
                        buffer[0] = TryInsertItem(target.handler, buffer[0]);
                    }
                    if (buffer[0] == null)
                    {
                        break;
                    }
                }
            }
            else // Limited insert logic
            {
                for (TargetResolver.Target<IInventory> target : targets) // Need to loop through because sometimes the full stack cannot fit in one inventory
                {
                    if (target.handler instanceof ISidedInventory sidedInventory) // Sided logic
                    {
                        buffer[0] = TryInsertItemSidedLimited(sidedInventory, buffer[0], target.side, insertLimit);
                    }
                    else // Basic logic
                    {
                        buffer[0] = TryInsertItemLimited(target.handler, buffer[0], insertLimit);
                    }
                    if (buffer[0] == null)
                    {
                        break;
                    }
                }
            }
        }
        walker.step();
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

    // Insertion logic for non-sided inventories where there's a maxAllowed in the inventory
    public ItemStack TryInsertItemLimited(IInventory inventory, ItemStack stack, int maxAllowed)
    {
        if (stack == null || stack.stackSize <= 0)
        {
            return null;
        }

        int size = inventory.getSizeInventory();

        // I'm using IntArrayLists to be the fastest.
        // Even entries are slot numbers and subsequent odd entries are the amount of items that can be put into them.
        IntArrayList mergeableSlots = new IntArrayList();
        IntArrayList emptySlots = new IntArrayList();

        int found = 0;

        for (int slot = 0; slot < size; slot++)
        {
            if (!inventory.isItemValidForSlot(slot, stack))
            {
                continue;
            }

            ItemStack existing = inventory.getStackInSlot(slot);
            if (existing == null)
            {
                emptySlots.add(slot);
                emptySlots.add(inventory.getInventoryStackLimit());
            }
            else if (canStacksMerge(existing, stack))
            {
                found += existing.stackSize;
                if (found >= maxAllowed)
                {
                    return stack;
                }

                int max = Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit());
                int space = max - existing.stackSize;
                if (space > 0)
                {
                    mergeableSlots.add(slot);
                    mergeableSlots.add(space);
                }
            }
        }

        int preMergeStackSize = stack.stackSize;
        // Once for mergeable slots first, then the empty slots.
        StackToInventoryMergingHelperLimited(mergeableSlots, inventory, stack, found, maxAllowed);
        if (stack.stackSize <= 0)
        {
            return null;
        }
        found += preMergeStackSize - stack.stackSize;
        return StackToInventoryMergingHelperLimited(emptySlots, inventory, stack, found, maxAllowed);
    }

    // Insertion logic for sided inventories where there's a maxAllowed in the inventory
    public ItemStack TryInsertItemSidedLimited(ISidedInventory inventory, ItemStack stack, int side, int maxAllowed)
    {
        if (stack == null || stack.stackSize <= 0)
        {
            return null;
        }

        int[] slots = inventory.getAccessibleSlotsFromSide(side);

        // I'm using IntArrayLists to be the fastest.
        // Even entries are slot numbers and subsequent odd entries are the amount of items that can be put into them.
        IntArrayList mergeableSlots = new IntArrayList();
        IntArrayList emptySlots = new IntArrayList();

        int found = 0;
        for (int slot : slots)
        {
            if (!inventory.canInsertItem(slot, stack, side))
            {
                continue;
            }

            ItemStack existing = inventory.getStackInSlot(slot);
            if (existing == null)
            {
                emptySlots.add(slot);
                emptySlots.add(inventory.getInventoryStackLimit());
            }
            else if (canStacksMerge(existing, stack))
            {
                found += existing.stackSize;
                if (found >= maxAllowed)
                {
                    return stack;
                }

                int max = Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit());
                int space = max - existing.stackSize;
                if (space > 0)
                {
                    mergeableSlots.add(slot);
                    mergeableSlots.add(space);
                }
            }
        }

        // Once for mergeable slots first, then the empty slots.
        int preMergeStackSize = stack.stackSize;
        StackToInventoryMergingHelperLimited(mergeableSlots, inventory, stack, found, maxAllowed);
        if (stack.stackSize <= 0)
        {
            return null;
        }
        found += preMergeStackSize - stack.stackSize;
        return StackToInventoryMergingHelperLimited(emptySlots, inventory, stack, found, maxAllowed);
    }

    // Helper for consistent inventory insertion/merging
    private ItemStack StackToInventoryMergingHelperLimited(IntArrayList slotInfo, IInventory inventory, ItemStack insertionStack, int currentItemsInInventory, int maxAllowedInInventory)
    {
        int insertAmount = maxAllowedInInventory - currentItemsInInventory;
        for (int i = 0; i < slotInfo.size(); i += 2)
        {
            int slot = slotInfo.getInt(i);
            int amountInsertable = Math.min(slotInfo.getInt(i + 1), Math.min(insertionStack.stackSize, insertAmount));
            insertionStack.stackSize -= amountInsertable;
            currentItemsInInventory += amountInsertable;
            if (inventory.getStackInSlot(slot) == null)
            {
                ItemStack newStack = insertionStack.copy();
                newStack.stackSize = amountInsertable;
                inventory.setInventorySlotContents(slot, newStack);
            }
            else
            {
                inventory.getStackInSlot(slot).stackSize += amountInsertable;
            }

            if (currentItemsInInventory == maxAllowedInInventory)
            {
                if (insertionStack.stackSize <= 0)
                {
                    return null;
                }
                else
                {
                    return insertionStack;
                }
            }
            if (insertionStack.stackSize <= 0)
            {
                return null;
            }
        }
        return insertionStack;
    }

    // Default sided insertion logic
    public ItemStack TryInsertItemSided(ISidedInventory inventory, ItemStack stack, int side)
    {
        if (stack == null || stack.stackSize <= 0)
        {
            return null;
        }

        int[] slots = inventory.getAccessibleSlotsFromSide(side);

        IntArrayList mergeableSlots = new IntArrayList();
        IntArrayList emptySlots = new IntArrayList();

        // Note that we have to iterate the whole inventory first or we won't know if there's mergable slots
        for (int slot : slots)
        {
            if (!inventory.canInsertItem(slot, stack, side))
            {
                continue;
            }

            ItemStack existing = inventory.getStackInSlot(slot);
            if (existing == null)
            {
                emptySlots.add(slot);
                emptySlots.add(inventory.getInventoryStackLimit());
            }
            else if (canStacksMerge(existing, stack))
            {
                int max = Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit());
                int space = max - existing.stackSize;
                if (space > 0)
                {
                    mergeableSlots.add(slot);
                    mergeableSlots.add(space);
                }
            }
        }
        StackToInventoryMergingHelper(mergeableSlots, inventory, stack);
        if (stack.stackSize <= 0)
        {
            return null;
        }
        return StackToInventoryMergingHelper(emptySlots, inventory, stack);
    }

    // Default insertion logic
    public ItemStack TryInsertItem(IInventory inventory, ItemStack stack)
    {
        if (stack == null || stack.stackSize <= 0)
        {
            return null;
        }

        int size = inventory.getSizeInventory();

        // I'm using IntArrayLists to be the fastest.
        // Even entries are slot numbers and subsequent odd entries are the amount of items that can be put into them.
        IntArrayList mergeableSlots = new IntArrayList();
        IntArrayList emptySlots = new IntArrayList();

        for (int slot = 0; slot < size; slot++)
        {
            if (!inventory.isItemValidForSlot(slot, stack))
            {
                continue;
            }

            ItemStack existing = inventory.getStackInSlot(slot);
            if (existing == null)
            {
                emptySlots.add(slot);
                emptySlots.add(inventory.getInventoryStackLimit());
            }
            else if (canStacksMerge(existing, stack))
            {


                int max = Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit());
                int space = max - existing.stackSize;
                if (space > 0)
                {
                    mergeableSlots.add(slot);
                    mergeableSlots.add(space);
                }
            }
        }
        StackToInventoryMergingHelper(mergeableSlots, inventory, stack);
        if (stack.stackSize <= 0)
        {
            return null;
        }
        return StackToInventoryMergingHelper(emptySlots, inventory, stack);
    }

    // Helper for consistent inventory insertion/merging
    private ItemStack StackToInventoryMergingHelper(IntArrayList slotInfo, IInventory inventory, ItemStack insertionStack)
    {
        for (int i = 0; i < slotInfo.size(); i += 2)
        {
            int slot = slotInfo.getInt(i);
            int amountInsertable = Math.min(slotInfo.getInt(i + 1), insertionStack.stackSize);
            insertionStack.stackSize -= amountInsertable;
            if (inventory.getStackInSlot(slot) == null)
            {
                ItemStack newStack = insertionStack.copy();
                newStack.stackSize = amountInsertable;
                inventory.setInventorySlotContents(slot, newStack);
            }
            else
            {
                inventory.getStackInSlot(slot).stackSize += amountInsertable;
            }

            if (insertionStack.stackSize <= 0)
            {
                return null;
            }
        }
        return insertionStack;
    }

    // Not sure if this is the correct way to do this tbh. I don't know if any of the built in
    // itemstack stuff is better.
    private static boolean canStacksMerge(ItemStack a, ItemStack b)
    {
        return a.getItem() == b.getItem()
            && a.getItemDamage() == b.getItemDamage()
            && ItemStack.areItemStackTagsEqual(a, b);
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
        if (buffer[index] == null)
        {
            return null;
        }
        return buffer[index].splitStack(count);
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

    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(UtilitiesInExcess.MODID, mainPanel);
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

    public void updateSourceInventory()
    {
        ForgeDirection facing = host.getFacing();
        TileEntity neighbor = host.getWorld().getTileEntity(host.getX() + facing.offsetX, host.getY() + facing.offsetY, host.getZ() + facing.offsetZ);
        if (neighbor instanceof IInventory inventory) {
            connectedInventory = inventory;
        }
    }
}
